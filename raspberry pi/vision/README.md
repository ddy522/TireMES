docker build -t tire-vision-api .

docker run -d -p 5000:5000 -e "MQTT_BROKER=192.168.1.3" vision

sudo docker run -d -p 5000:5000 -e "MQTT_BROKER=192.168.200.204" -e "MQTT_PORT=1883" -v "$(pwd)/test:/app/test" --name vision-container tire-vision-api

sudo docker run -d -p 5000:5000 \
-e "MQTT_BROKER=192.168.200.204" \
-e "MQTT_PORT=1883" \
-v "/home/back/vision/test:/app/test" \
--add-host=host.docker.internal:192.168.200.204 \
--name vision-container tire-vision-api

sudo docker rm -f vision-container