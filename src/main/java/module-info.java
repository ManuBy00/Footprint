module com.example.footprinttracker {
    requires javafx.controls;
    requires javafx.fxml;

    // --- Hibernate & JPA ---
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    // --- LO QUE TE FALTABA (SOLUCIONA TU ERROR) ---
    requires java.naming; // <--- Soluciona el error "Referenceable not found"
    requires java.sql;
    requires jakarta.transaction;
    requires javafx.graphics;
    requires jbcrypt;
    //requires com.example.footprinttracker;    // <--- Necesario para conectar con la Base de Datos

    // --- PERMISOS DE REFLEXIÓN ---

    // 1. Para JavaFX (ya lo tenías)
    opens com.example.footprinttracker to javafx.fxml;
    opens com.example.footprinttracker.view to javafx.fxml;

    // 2. PARA HIBERNATE (¡CRUCIAL!)
    // Debes abrir el paquete donde están tus clases @Entity (Usuario, Huella...).
    // Si tus entidades están en 'com.example.footprinttracker.model', pon eso.
    // Si están en la raíz del paquete, usa la línea de abajo:
    opens com.example.footprinttracker.Model to org.hibernate.orm.core;

    opens com.example.footprinttracker.Controller to javafx.fxml;

    // Si tienes las entidades en una subcarpeta (ej. model), añade también:
    // opens com.example.footprinttracker.model to org.hibernate.orm.core;

    exports com.example.footprinttracker;



}