package com.ego.auth.config;

import com.ego.auth.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ego.jwt")
public class JwtProperties {
    private String secret; // 密钥
    private String pubKeyPath;// 公钥
    private String priKeyPath;// 私钥
    private int expire;// token过期时间
    private PublicKey publicKey; // 公钥
    private PrivateKey privateKey; // 私钥
    private String cookieName; //cookie名字
    private Integer cookieMaxAge; //cookie有效期 <秒>
    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);
    /**
     * @PostContruct：在构造方法执行之后执行该方法
     */
    @PostConstruct
    public void init(){
        try {
            File pubKey = new File(pubKeyPath);
            File priKey = new File(priKeyPath);
            if (!pubKey.exists() || !priKey.exists()) {
            // 生成公钥和私钥
                RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            // 获取公钥和私钥
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }
}

