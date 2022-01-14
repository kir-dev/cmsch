package hu.bme.sch.cmsch.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.util.sha256
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import javax.annotation.PostConstruct


@Service
class UserProfileGeneratorService(
        @Value("\${cmsch.profile.qr-prefix:KIRDEV_}") val prefix: String,
        @Value("\${cmsch.profile.salt:60_LONG_STRING}") private val salt: String,
        @Value("\${cmsch.profile.generation-target:/etc/cmsch/external/profiles}") private val rootPath: String,
        @Value("\${cmsch.profile.qr-code-size:360}") private val size: Int
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        File(rootPath).mkdirs()
    }

    @Throws(WriterException::class, IOException::class)
    private fun createQR(data: String, path: String) {
        val matrix = MultiFormatWriter().encode(
                String(data.toByteArray(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
                BarcodeFormat.QR_CODE,
                size, size,
                mutableMapOf(
                        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                        EncodeHintType.CHARACTER_SET to StandardCharsets.UTF_8.toString(),
                        EncodeHintType.MARGIN to 1
                )
        )

        MatrixToImageWriter.writeToPath(matrix,
                path.substring(path.lastIndexOf('.') + 1),
                Path.of(path))
    }

    fun generateProfileForUser(user: UserEntity) {
        user.g7id = (prefix + (user.pekId + salt).sha256().substring(prefix.length, 40))
        val fullPath = rootPath + File.separator + user.g7id + ".png"
        if (Files.exists(Path.of(fullPath))) {
            log.info("QR code already exists for user ${user.fullName}")
            return
        }
        createQR(user.g7id, fullPath)
        log.info("New QR code was generated to $fullPath for user ${user.fullName}")
    }

}
