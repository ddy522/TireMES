// com.example.demo.dto.VisionDecisionDTO.java
package com.example.demo.model;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VisionDecisionModel {
    private Long id;
    private String workNo;
    private Integer inspectionResult; // 1=OK, 0=NG, null=미판정
    private String imageUrl;

    public String getDecisionText() {
        if (inspectionResult == null) return "미판정";
        return inspectionResult == 1 ? "OK" : "NG";
    }
}
