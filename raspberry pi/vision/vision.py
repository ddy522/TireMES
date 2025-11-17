from flask import Flask, jsonify, send_from_directory
from flask_cors import CORS
import torch
import torch.nn as nn
import torch.nn.functional as F
from torchvision import transforms
from PIL import Image
import os
import random
import json
import paho.mqtt.client as mqtt
from datetime import datetime

app = Flask(__name__)
CORS(app)  # Vue에서 API 호출 허용

# 모델 클래스
class TireDefectCNN(nn.Module):
    def __init__(self, num_classes=2):
        super(TireDefectCNN, self).__init__()
        self.conv1 = nn.Conv2d(3, 32, kernel_size=3, padding=1)
        self.bn1 = nn.BatchNorm2d(32)
        self.pool1 = nn.MaxPool2d(2, 2)
        self.conv2 = nn.Conv2d(32, 64, kernel_size=3, padding=1)
        self.bn2 = nn.BatchNorm2d(64)
        self.pool2 = nn.MaxPool2d(2, 2)
        self.conv3 = nn.Conv2d(64, 128, kernel_size=3, padding=1)
        self.bn3 = nn.BatchNorm2d(128)
        self.pool3 = nn.MaxPool2d(2, 2)
        self.conv4 = nn.Conv2d(128, 256, kernel_size=3, padding=1)
        self.bn4 = nn.BatchNorm2d(256)
        self.pool4 = nn.MaxPool2d(2, 2)
        self.flatten = nn.Flatten()
        self.dropout1 = nn.Dropout(0.5)
        self.fc1 = nn.Linear(256 * 8 * 8, 512)
        self.bn5 = nn.BatchNorm1d(512)
        self.dropout2 = nn.Dropout(0.3)
        self.fc2 = nn.Linear(512, 128)
        self.dropout3 = nn.Dropout(0.2)
        self.fc3 = nn.Linear(128, num_classes)
        
    def forward(self, x):
        x = self.pool1(F.relu(self.bn1(self.conv1(x))))
        x = self.pool2(F.relu(self.bn2(self.conv2(x))))
        x = self.pool3(F.relu(self.bn3(self.conv3(x))))
        x = self.pool4(F.relu(self.bn4(self.conv4(x))))
        x = self.flatten(x)
        x = self.dropout1(x)
        x = F.relu(self.bn5(self.fc1(x)))
        x = self.dropout2(x)
        x = F.relu(self.fc2(x))
        x = self.dropout3(x)
        x = self.fc3(x)
        return x

# 전역 변수
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
model = None
transform = transforms.Compose([
    transforms.Resize((128, 128)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
])

# MQTT 설정 (환경 변수에서 값 읽어오기)
MQTT_BROKER = "192.168.200.204" 
MQTT_PORT = int(os.environ.get("MQTT_PORT", 1883))
MQTT_TOPIC = "tire/prediction"  
mqtt_client = None

# MQTT 설정
def setup_mqtt():
    global mqtt_client
    try:
        mqtt_client = mqtt.Client()
        mqtt_client.on_connect = on_mqtt_connect
        mqtt_client.connect(MQTT_BROKER, MQTT_PORT, 60)
        mqtt_client.loop_start()
        return True
    except Exception as e:
        print(f"❌ MQTT 연결 실패: {e}")
        return False

def on_mqtt_connect(client, userdata, flags, rc):
    if rc == 0:
        print("✅ MQTT 브로커 연결 성공")
    else:
        print(f"❌ MQTT 연결 실패: {rc}")

def publish_to_mqtt(data):
    global mqtt_client
    if mqtt_client and mqtt_client.is_connected():
        try:
            message = json.dumps(data, ensure_ascii=False)
            mqtt_client.publish(MQTT_TOPIC, message)
            print(f"📡 MQTT 발행 성공: {len(message)} bytes")
            return True
        except Exception as e:
            print(f"❌ MQTT 발행 실패: {e}")
            return False
    else:
        print("❌ MQTT 연결 안됨")
        return False

# 모델 로드
def load_model():
    global model
    try:
        # 모델 클래스가 전역으로 정의되어 있어 weights_only=False 로드 시 필요.
        # torch.serialization.add_safe_globals([TireDefectCNN])
        model = torch.load('tire_model_complete.pth', map_location=device, weights_only=False)
        model.eval()
        return True
    except Exception as e:
        print(f"❌ 모델 로드 실패: {e}")
        return False

# 예측 함수
def predict_image(image_path):
    try:
        image = Image.open(image_path).convert('RGB')
        input_tensor = transform(image).unsqueeze(0).to(device)
        
        with torch.no_grad():
            outputs = model(input_tensor)
            probs = F.softmax(outputs, dim=1)
            defective_prob = probs[0][0].item()
            good_prob = probs[0][1].item()
            
            if defective_prob > good_prob:
                prediction = "불량"
                confidence = defective_prob
            else:
                prediction = "정상"
                confidence = good_prob
        
        filename = os.path.basename(image_path)
        image_url = f"http://localhost:5000/images/{filename}"
        
        return {
            'filename': filename,
            'image_url': image_url,
            'prediction': prediction,
            'confidence': round(confidence, 3),
            'defective_prob': round(defective_prob, 3),
            'good_prob': round(good_prob, 3)
        }
    except Exception as e:
        return {'filename': os.path.basename(image_path), 'error': str(e)}

# 예측 및 MQTT 발행
def run_prediction_and_publish(count):
    test_folder = 'test'
    if not os.path.exists(test_folder):
        return {'error': 'test folder not found'}
    
    image_files = [f for f in os.listdir(test_folder) 
                   if f.lower().endswith(('.jpg', '.jpeg', '.png', '.bmp'))]
    
    if not image_files:
        return {'error': 'no images in test folder'}
    
    actual_count = min(count, len(image_files))
    selected = random.sample(image_files, actual_count)
    
    results = []
    defective_count = 0
    
    for filename in selected:
        image_path = os.path.join(test_folder, filename)
        result = predict_image(image_path)
        results.append(result)
        
        if 'prediction' in result and result['prediction'] == '불량':
            defective_count += 1
    
    good_count = actual_count - defective_count
    
    final_result = {
        'request_count': count,
        'actual_count': actual_count,
        'defective_count': defective_count,
        'good_count': good_count,
        'defective_percentage': round(defective_count / actual_count * 100, 1) if actual_count > 0 else 0,
        'results': results,
        'timestamp': datetime.now().isoformat(),
        'status': 'success'
    }
    
    # MQTT 발행
    mqtt_success = publish_to_mqtt(final_result)
    final_result['mqtt_published'] = mqtt_success
    
    return final_result

# API 엔드포인트들

@app.route('/api/predict/<int:count>', methods=['GET', 'POST'])
def api_predict(count):
    """Vue에서 호출할 예측 API"""
    if not model:
        return jsonify({'error': 'model not loaded', 'status': 'error'}), 500
    
    if count <= 0 or count > 100:
        return jsonify({'error': 'count must be 1-100', 'status': 'error'}), 400
    
    print(f"🔄 예측 요청 받음: {count}개")
    result = run_prediction_and_publish(count)
    print(f"✅ 예측 완료: {result.get('defective_count', 0)}개 불량")
    
    return jsonify(result)

@app.route('/api/health', methods=['GET'])
def health_check():
    """서버 상태 확인"""
    return jsonify({
        'status': 'healthy',
        'model_loaded': model is not None,
        'mqtt_connected': mqtt_client is not None and mqtt_client.is_connected(),
        'device': str(device),
        'timestamp': datetime.now().isoformat()
    })

@app.route('/images/<filename>', methods=['GET'])
def serve_image(filename):
    """이미지 파일 서빙"""
    return send_from_directory('test', filename)

@app.route('/api/status', methods=['GET'])
def get_status():
    """상세 상태 정보"""
    test_folder = 'test'
    image_count = 0
    if os.path.exists(test_folder):
        image_files = [f for f in os.listdir(test_folder) 
                       if f.lower().endswith(('.jpg', '.jpeg', '.png', '.bmp'))]
        image_count = len(image_files)
    
    return jsonify({
        'server_status': 'running',
        'model_loaded': model is not None,
        'mqtt_connected': mqtt_client is not None and mqtt_client.is_connected(),
        'device': str(device),
        'test_images_count': image_count,
        'mqtt_broker': MQTT_BROKER,
        'mqtt_topic': MQTT_TOPIC,
        'timestamp': datetime.now().isoformat()
    })

# 메인 실행
if __name__ == '__main__':
    print("🚀 타이어 예측 API 서버 (백엔드 전용)")
    print("="*50)
    
    # 모델 로드
    model_loaded = load_model()
    print(f"모델: {'✅ 로드됨' if model_loaded else '❌ 실패'}")
    
    # MQTT 연결
    mqtt_connected = setup_mqtt()
    print(f"MQTT: {'✅ 연결됨' if mqtt_connected else '❌ 실패'}")
    
    if model_loaded:
        print(f"\n📡 API 엔드포인트:")
        print(f"GET/POST /api/predict/<count>  - 예측 실행")
        print(f"GET /api/health                - 서버 상태")
        print(f"GET /api/status                - 상세 상태")
        print(f"GET /images/<filename>         - 이미지 서빙")
        print(f"\n🌐 서버 실행: http://localhost:5000")
        print(f"📡 MQTT 브로커: {MQTT_BROKER}")
        print(f"📡 MQTT 토픽: {MQTT_TOPIC}")
        print("="*50)
        
        app.run(host='0.0.0.0', port=5000, debug=False)
    else:
        print("❌ 모델 로드 실패")