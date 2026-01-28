package com.example.footprinttracker.Services;

import com.example.footprinttracker.DAO.ActividadDAO;
import com.example.footprinttracker.DAO.HabitoDAO;
import com.example.footprinttracker.Model.Actividad;
import com.example.footprinttracker.Model.Habito;
import com.example.footprinttracker.Model.Usuario;

import java.util.List;

public class HabitosService {
    private HabitoDAO habitoDAO = new HabitoDAO();
    private ActividadDAO actividadDAO = new ActividadDAO();

    public List<Habito> listaHabitosUsuario(Usuario usuario){
        return habitoDAO.getHabitosUsuario(usuario);
    }

    public List<Actividad> listaActividades(){
        return actividadDAO.getActividades();
    }

    public void addHabito(Habito habito){
        habitoDAO.addHabito(habito);
    }

    public void deleteHabito(Habito habito){
        habitoDAO.deleteHabito(habito);
    }
}
