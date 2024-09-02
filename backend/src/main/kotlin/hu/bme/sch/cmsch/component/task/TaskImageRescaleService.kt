package hu.bme.sch.cmsch.component.task

import org.springframework.stereotype.Service
import java.awt.Image
import java.awt.image.BufferedImage

@Service
class TaskImageRescaleService {

    fun resizeImage(image: BufferedImage, maxWidth: Int, maxHeight: Int): BufferedImage {
        val width = image.width
        val height = image.height

        val scalingFactor = minOf(maxWidth.toDouble() / width, maxHeight.toDouble() / height)

        val newWidth = (width * scalingFactor).toInt()
        val newHeight = (height * scalingFactor).toInt()

        val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)

        val graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null)
        graphics2D.dispose()

        return resizedImage
    }

}