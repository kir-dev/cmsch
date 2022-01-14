package hu.bme.sch.cmsch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CMSchApplication

fun main(args: Array<String>) {
    runApplication<CMSchApplication>(*args)
}
