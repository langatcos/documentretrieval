package com.dmeDocuments.dmeDocuments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DmeDocumentsApplication

fun main(args: Array<String>) {
	runApplication<DmeDocumentsApplication>(*args)
}
