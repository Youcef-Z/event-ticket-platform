package com.youcef.tickets.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.youcef.tickets.domain.entities.QrCode;
import com.youcef.tickets.domain.entities.QrCodeStatusEnum;
import com.youcef.tickets.domain.entities.Ticket;
import com.youcef.tickets.exceptions.QrCodeGenerationException;
import com.youcef.tickets.repositories.QrCodeRepository;
import com.youcef.tickets.services.QrCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private static final int QR_HEIGHT = 300;
    private static final int QR_WIDTH = 300;

    private final QrCodeRepository qrCodeRepository;
    private final QRCodeWriter qrCodeWriter;

    @Override
    public QrCode generateQrCode(Ticket ticket) {
        try {
            UUID uniqueId = UUID.randomUUID();
            String qrCodeImage =generateQrCodeImage(uniqueId);

            QrCode qrCode = new QrCode();
            qrCode.setId(uniqueId);
            qrCode.setStatus(QrCodeStatusEnum.ACTIVE);
            qrCode.setValue(qrCodeImage);
            qrCode.setTicket(ticket);

            // Flushing means the QR code will be immediately persisted to the database.
            // This is often done when you need the generated ID right away or need to ensure the data is
            // immediately available to other database operations or transactions.
            // If immediate persistence isn't necessary, save() would be more efficient as it reduces the number of database round trips.
            return qrCodeRepository.saveAndFlush(qrCode);
        } catch (WriterException | IOException e) {
            throw new QrCodeGenerationException("Unable to generate QR Code", e);
        }
    }

    private String generateQrCodeImage(UUID uniqueId) throws WriterException, IOException {
        BitMatrix bitMatrix = qrCodeWriter.encode(uniqueId.toString(), BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
        BufferedImage QrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(QrCodeImage, "png", baos);
            byte[] imageInByte = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageInByte);
        }
    }
}
