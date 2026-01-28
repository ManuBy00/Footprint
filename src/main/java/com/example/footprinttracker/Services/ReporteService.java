package com.example.footprinttracker.Services;

import com.example.footprinttracker.DAO.HuellaDAO;
import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Utils.Sesion;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class ReporteService {

    /**
     * Genera un archivo CSV utilizando Streams para procesar la lista. Convierte la lista de huellas en un stream de strings para escribirlas.
     * @param rutaArchivo Ruta completa donde se guardará el archivo.
     */
    public void generarCSV(String rutaArchivo) throws IOException {
        HuellaDAO huellaDAO = new HuellaDAO();
        List<Huella> historialHuellas = huellaDAO.getHuellasByUsuario(Sesion.getInstance().getUsuarioIniciado());

        Stream<String> streamCabecera = Stream.of("Fecha,Actividad,Valor,Unidad");

        //Creamos un stream con los datos de las huellas. Para ello, mapeamos el historial de huellas y obtenemos los datos a exportar de cada una.
        Stream<String> streamDatos = historialHuellas.stream().map(huella -> {
            return String.format("%s,%s,%d,%s",
                    huella.getFecha(),
                    huella.getIdActividad().getNombreActividad(),
                    huella.getValor(),
                    huella.getUnidad()
            );
        });

        // 3. UNIR ambos streams (Cabecera + Datos) y convertirlos a una Lista
        List<String> lineasParaEscribir = Stream.concat(streamCabecera, streamDatos)
                .toList();
        // 4. ESCRIBIR todo al disco de una sola vez
        // Files.write se encarga de crear, abrir, escribir y cerrar el archivo
        Path path = Paths.get(rutaArchivo);
        Files.write(path, lineasParaEscribir);
    }

}