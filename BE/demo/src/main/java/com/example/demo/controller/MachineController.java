// 4. Controller - MachineController.java
package com.example.demo.controller;

import com.example.demo.model.Machine;
import com.example.demo.service.MachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/machine")
@CrossOrigin(origins = "*")
public class MachineController {

    private static final Logger logger = LoggerFactory.getLogger(MachineController.class);

    @Autowired
    private MachineService machineService;

    /**
     * machine_name으로 machine_code 조회
     *
     * @param machineName 기계명
     * @return Machine 정보 (machine_code 포함)
     *
     * 사용 예시:
     * GET /api/machine/code?machineName=믹싱기
     */
    @GetMapping("/code")
    public ResponseEntity<Machine> getMachineCode(
            @RequestParam("machineName") String machineName) {

        logger.info("Machine 코드 조회 요청 - machineName: {}", machineName);

        try {
            // 입력값 검증
            if (machineName == null || machineName.trim().isEmpty()) {
                logger.warn("machineName 파라미터가 비어있음");
                return ResponseEntity.badRequest().build();
            }

            Machine machine = machineService.getMachineByName(machineName.trim());

            if (machine == null) {
                logger.info("해당 기계명에 대한 데이터를 찾을 수 없음 - machineName: {}", machineName);
                return ResponseEntity.notFound().build();
            }

            logger.info("Machine 코드 조회 완료 - machineCode: {}", machine.getMachineCode());

            return ResponseEntity.ok(machine);

        } catch (Exception e) {
            logger.error("Machine 코드 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * machine_code만 반환하는 간단한 API (선택적)
     */
    @GetMapping("/code-only")
    public ResponseEntity<String> getMachineCodeOnly(
            @RequestParam("machineName") String machineName) {

        logger.info("Machine 코드만 조회 요청 - machineName: {}", machineName);

        try {
            if (machineName == null || machineName.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Machine machine = machineService.getMachineByName(machineName.trim());

            if (machine == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(machine.getMachineCode());

        } catch (Exception e) {
            logger.error("Machine 코드 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 헬스체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Machine API is running");
    }
}