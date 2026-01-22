package com.example.footprinttracker.Services;

import com.example.footprinttracker.DAO.ActividadDAO;
import com.example.footprinttracker.DAO.CategoriaDAO;
import com.example.footprinttracker.DAO.HuellaDAO;
import com.example.footprinttracker.Model.Actividad;
import com.example.footprinttracker.Model.Categoria;
import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Utils.Sesion;

import java.time.LocalDate;
import java.util.List;

public class HuellaService {
    Usuario usuarioLoged = Sesion.getInstance().getUsuarioIniciado();
    HuellaDAO dao = new HuellaDAO();
    CategoriaDAO cDao = new CategoriaDAO();
    ActividadDAO aDao = new ActividadDAO();


    public boolean addHuella(Categoria categoria, Actividad actividad, Integer cantidad, String unidad) {
        Huella huella = new Huella(categoria, actividad, cantidad, unidad);
        if (dao.addHuella(huella)){
            return true;
        }else {
            return  false;
        }
    }

    public List<Huella> cargarHuellasUsuario(){
        return dao.getHuellasByUsuario(usuarioLoged);
    }

    public List<Categoria> cargarCategorias(){
        return cDao.getAllCategorias();
    }

    public List<Actividad> cargarActividadesPorCategoria(Categoria categoria){
        return aDao.getActividadesPorCategoria(categoria);
    }

    public boolean deleteHuella(Huella huella) {
        if(dao.deleteHuella(huella)){
            return true;
        }else {
            return false;
        }
    }

    public double calcularHuellaMensual(int mes, int year){
        List<Huella> huellas = dao.obtenerHuellasMensual(mes, year);
        double impacto = 0;
        // DEBUG: Comprueba si realmente hay huellas
        System.out.println("Huellas encontradas este mes: " + huellas.size());
        for (Huella huella : huellas){
            System.out.println("valor de la huella: " + huella.getValor() + "\n valor de factor: " + huella.getIdActividad().getIdCategoria().getFactorEmision());
            impacto += huella.getValor() * huella.getIdActividad().getIdCategoria().getFactorEmision();
        }
        return impacto;
    }

    public int registroMensual(){
        LocalDate fecha = LocalDate.now();
        int mes = fecha.getMonth().getValue();
        int year = fecha.getYear();
        int numRegistros =  dao.obtenerHuellasMensual(mes, year).toArray().length;
        return numRegistros;
    }
}
