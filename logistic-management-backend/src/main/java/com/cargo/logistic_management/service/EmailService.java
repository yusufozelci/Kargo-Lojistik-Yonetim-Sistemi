package com.cargo.logistic_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yusufozelci2005@gmail.com");
        message.setTo(toEmail);
        message.setSubject("NovaKargo | Şifre Sıfırlama Kodu");
        message.setText("Merhaba,\n\nŞifre sıfırlama talebiniz alınmıştır.\n\n"
                + "Doğrulama Kodunuz: " + otpCode + "\n\n"
                + "Bu kod 15 dakika boyunca geçerlidir. Lütfen kimseyle paylaşmayınız.");

        mailSender.send(message);
    }

    public void sendPasswordChangeNotification(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("yusufozelci2005@gmail.com");
        message.setTo(toEmail);
        message.setSubject("NovaKargo | Güvenlik Uyarısı: Şifreniz Değiştirildi");
        message.setText("Merhaba,\n\n"
                + "NovaKargo hesabınızın şifresi kısa süre önce başarıyla değiştirilmiştir.\n\n"
                + "Eğer bu işlemi siz YAPMADIYSANIZ, hesabınızın güvenliği için derhal sistem yöneticisiyle iletişime geçiniz.\n\n"
                + "Güvenli günler dileriz,\nNovaKargo Güvenlik Ekibi");

        mailSender.send(message);
    }
}