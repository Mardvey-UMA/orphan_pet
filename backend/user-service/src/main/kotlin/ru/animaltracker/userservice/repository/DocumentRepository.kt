package ru.animaltracker.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.animaltracker.userservice.entity.Document

interface DocumentRepository : JpaRepository<Document, Long>