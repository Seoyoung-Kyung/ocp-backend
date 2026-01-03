package com.ocp.ocp_finalproject.workflow.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AesCryptoUtil {

    /**
     * 사용할 암호화 알고리즘
     * AES + GCM 모드 + 패딩 없음
     *
     * - AES : 대칭키 암호화 표준
     * - GCM : 암호화 + 무결성(위변조 방지) 제공
     * - NoPadding : GCM은 패딩이 필요 없음
     */
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    /**
     * GCM 모드에서 권장되는 IV(초기화 벡터) 길이
     * - 12바이트(96bit)가 표준
     * - 매 암호화마다 새로 생성해야 함
     */
    private static final int IV_SIZE = 12;

    /**
     * 인증 태그(Tag) 길이
     * - 128bit가 표준이자 가장 안전
     * - 암호문이 위변조되었는지 검증하는 데 사용됨
     */
    private static final int TAG_SIZE = 128;

    /**
     * 실제 AES 암호화/복호화에 사용될 비밀 키
     * - application.yml / 환경 변수에서 Base64 문자열로 주입받아
     * - 다시 byte[]로 복원하여 SecretKey로 생성
     */
    private final SecretKey secretKey;

    /**
     * 생성자
     *
     * @param base64Key
     *   - Base64로 인코딩된 AES 키 문자열
     *   - 예: mF3d9pE6mZz0Xv8N8qG4z6s4Yc+2n1GZQ0r3Vw7kX5M=
     */
    public AesCryptoUtil(
            @Value("${app.crypto.secret-key}") String base64Key
    ) {
        // Base64 문자열 → 실제 바이트 키로 디코딩
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);

        // 디코딩된 바이트를 AES 전용 SecretKey 객체로 변환
        this.secretKey = new SecretKeySpec(decodedKey, "AES");
    }

    /**
     * 평문을 AES-GCM 방식으로 암호화
     *
     * @param plainText 암호화할 원본 문자열
     * @return Base64로 인코딩된 암호문 (IV + 암호 데이터 포함)
     */
    public String encrypt(String plainText) {
        try {
            // 매 암호화마다 새 IV 생성 (보안상 필수)
            byte[] iv = new byte[IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            // AES/GCM 암호화 객체 생성
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // GCM 파라미터 설정 (태그 길이 + IV)
            GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);

            // 암호화 모드로 초기화
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            // 실제 암호화 수행
            byte[] encrypted = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8)
            );

            /**
             * IV + 암호문을 하나로 합침
             * 이유:
             * - 복호화 시 IV가 반드시 필요
             * - 별도 컬럼으로 저장하지 않기 위해 함께 저장
             */
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);

            // DB 저장을 위해 다시 Base64 문자열로 변환
            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (Exception e) {
            throw new IllegalStateException("암호화 실패", e);
        }
    }

    /**
     * 암호문을 AES-GCM 방식으로 복호화
     *
     * @param cipherText encrypt()로 생성된 Base64 문자열
     * @return 복호화된 원본 문자열
     */
    public String decrypt(String cipherText) {
        try {
            // Base64 문자열 → IV + 암호문 바이트 배열
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);

            // 앞부분에서 IV 추출
            byte[] iv = new byte[IV_SIZE];
            buffer.get(iv);

            // 나머지는 실제 암호 데이터
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);

            // AES/GCM 복호화 객체 생성
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_SIZE, iv);

            // 복호화 모드로 초기화
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            // 복호화 수행
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            // 키가 다르거나, 암호문이 변조되었으면 여기서 예외 발생
            throw new IllegalStateException("복호화 실패", e);
        }
    }

    /*
     * 사용 예시
     *
     * 암호화:
     * String encryptedPassword = aesCryptoUtil.encrypt(workflowRequest.getBlogAccountPwd());
     *
     * 복호화:
     * String rawPwd = aesCryptoUtil.decrypt(userBlog.getAccountPassword());
     *
     * 복호화된 값은 외부 API 호출 등 내부 로직에서만 사용하고
     * 절대 Controller Response로 반환하면 안 됨
     */
}
