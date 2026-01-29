package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.DTO.CategoriaEstadistica;
import com.example.footprinttracker.DTO.ImpactoCategoria;
import com.example.footprinttracker.DTO.ImpactoMensual;

import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Utils.Sesion;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


public class HuellaDAO {

    /**
     * Persiste una nueva huella de carbono en la base de datos.
     * Inicia una transacción y guarda el objeto. Si ocurre un error, realiza un rollback.
     *
     * @param huella El objeto Huella a guardar.
     * @return true si se guardó correctamente, false si ocurrió un error.
     */
    public boolean addHuella(Huella huella) {
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            session.persist(huella);
            tx.commit();
            return true;
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una huella existente de la base de datos.
     * Busca la huella dentro de la sesión actual antes de eliminarla
     *
     * @param huella El objeto Huella que contiene a borrar.
     * @return true si se eliminó correctamente, false si no se encontró o hubo error.
     */
    public boolean deleteHuella(Huella huella) {
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            Huella h = session.get(Huella.class, huella.getId());
            if (h != null) {
                session.remove(h);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
            return false;
        }
    }


    /**
     * Actualiza los datos de una huella existente.
     * Utiliza 'merge' para actualizar el registro o volver a adjuntarlo a la sesión si estaba desconectado.
     *
     * @param huella El objeto Huella con los datos modificados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean updateHuella(Huella huella) {
        boolean updated = false;
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            session.merge(huella);
            tx.commit();
            updated = true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return updated;
    }


    /**
     * Obtiene el historial completo de huellas de un usuario específico.
     * Utiliza JOIN FETCH para carga eager de la Actividad
     * y la Categoría asociadas
     *
     * @param usuario El usuario del cual se quieren obtener las huellas.
     * @return Una lista de objetos Huella ordenados por fecha descendente (más recientes primero).
     */
    public List<Huella> getHuellasByUsuario(Usuario usuario) {
        try (Session session = ConnectionDB.getInstance().getSession()) {

            // "JOIN FETCH h.idActividad a" -> Carga la Actividad y le pone el alias 'a'
            //  "JOIN FETCH a.idCategoria"   -> Usa el alias 'a' para cargar también la Categoría
            String hql = "FROM Huella h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :uid " +
                    "ORDER BY h.fecha DESC";

            Query<Huella> query = session.createQuery(hql, Huella.class);
            query.setParameter("uid", usuario.getId());

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Filtra las huellas del usuario actualmente logueado por mes y año específicos.
     * Ideal para vistas de calendario o reportes mensuales.
     *
     * @param mes El número del mes (1-12).
     * @param year El año (ej. 2023).
     * @return Lista de huellas filtradas y ordenadas por fecha.
     */
    public List<Huella> obtenerHuellasMensual(int mes, int year) {
        try (Session session = ConnectionDB.getInstance().getSession()){
            Usuario usuario = Sesion.getInstance().getUsuarioIniciado();
            String hql = "From Huella h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :uid " +
                    "AND month(h.fecha) = :mes " +
                    "AND year(h.fecha) = :year " +
                    "ORDER BY h.fecha DESC";

            Query<Huella> query = session.createQuery(hql, Huella.class);
            query.setParameter("uid", usuario.getId());
            query.setParameter("mes", mes);
            query.setParameter("year", year);

            return query.list();
        }
    }

    //ESTADISTICAS

    /**
     * Calcula el impacto total de la huella de carbono de un usuario en un rango de fechas específico.
     * Realiza la conversión de {@code LocalDate} a {@code Instant} para asegurar la precisión:

     * Aplica la fórmula: Valor Huella * Factor Emisión
     *
     * @param usuario    El usuario del que se consultan las huellas.
     * @param inicioDate Fecha de inicio del rango
     * @param finDate    Fecha de fin del rango
     * @return El sumatorio total de kg de CO₂ (Double). Devuelve 0.0 si no hay registros.
     */
    public Double obtenerImpactoEnRango(Usuario usuario, LocalDate inicioDate, LocalDate finDate) {
        Double total = 0.0;

        // 1. Conversión de fechas
        ZoneId zona = ZoneId.systemDefault();
        Instant inicio = inicioDate.atStartOfDay(zona).toInstant();
        Instant fin = finDate.atTime(LocalTime.MAX).atZone(zona).toInstant();

        // 2. LA FÓRMULA
        String hql = "SELECT SUM(h.valor * c.factorEmision) " +
                "FROM Huella h " +
                "JOIN h.idActividad a " +       // Unimos con Actividad
                "JOIN a.idCategoria c " +     // Unimos con Categoria (donde está el factor)
                "WHERE h.idUsuario = :uid " +
                "AND h.fecha BETWEEN :inicio AND :fin";

        try (Session session = ConnectionDB.getInstance().getSession()) {
            // Al haber una multiplicación por un decimal, Hibernate ya sabe que devuelve Double
            Query<Double> query = session.createQuery(hql, Double.class);

            query.setParameter("uid", usuario);
            query.setParameter("inicio", inicio);
            query.setParameter("fin", fin);

            Double resultado = query.uniqueResult();

            if (resultado != null) {
                total = resultado;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }


    /**
     * Recupera la distribución de frecuencia de las huellas agrupadas por categoría.
     * Utiliza un DTO de apoyo para agrupar los datos mediante HQL.
     * Esta técnica permite que Hibernate transforme automáticamente el resultado (Texto + Número)
     * en instancias de la clase

     * @param usuario El usuario cuyos datos se van a analizar.
     * @return Una lista tipada de CategoriaEstadistica, lista para usar en el PieChart.
     */
    public List<CategoriaEstadistica> obtenerDistribucionCategorias(Usuario usuario) {
        String hql = "SELECT new com.example.footprinttracker.DTO.CategoriaEstadistica(c.nombre, COUNT(h)) " +
                "FROM Huella h " +
                "JOIN h.idActividad a " +
                "JOIN a.idCategoria c " +
                "WHERE h.idUsuario = :uid " +
                "GROUP BY c.nombre";

        try (Session session = ConnectionDB.getInstance().getSession()) {
            Query<CategoriaEstadistica> query = session.createQuery(hql, CategoriaEstadistica.class);
            query.setParameter("uid", usuario);
            return query.list();
        }
    }


    /**
     * Obtiene la evolución mensual del impacto de carbono para un año específico.

     * @param usuario El usuario actual.
     * @param anio    El año a filtrar (ej: 2026).
     * @return Lista de objetos ImpactoMensual con el mes y el total calculado.
     */
    public List<ImpactoMensual> obtenerImpactoMensual(Usuario usuario, int anio) {
        String hql = "SELECT new " + ImpactoMensual.class.getName() + "(" +
                "   month(h.fecha), " +               // Mes (Integer)
                "   SUM(h.valor * c.factorEmision) " + // Impacto (Double)
                ") " +
                "FROM Huella h " +
                "JOIN h.idActividad a " +
                "JOIN a.idCategoria c " +
                "WHERE h.idUsuario = :uid " +
                "  AND year(h.fecha) = :anio " +      // Filtro por año directo
                "GROUP BY month(h.fecha) " +          // Agrupamos por mes
                "ORDER BY month(h.fecha)";            // Ordenamos Cronológicamente

        try (Session session = ConnectionDB.getInstance().getSession()) {
            Query<ImpactoMensual> query = session.createQuery(hql, ImpactoMensual.class);
            query.setParameter("uid", usuario);
            query.setParameter("anio", anio);

            return query.list();
        }
    }

    /**
     * Obtiene el impacto total de un usuario desglosado por categoría.
     */
    public List<ImpactoCategoria> obtenerImpactoPorCategoriaUsuario(Usuario usuario) {
        String hql = "SELECT new com.example.footprinttracker.DTO.ImpactoCategoria(" +
                "   c.nombre, " +
                "   SUM(h.valor * c.factorEmision) " +
                ") " +
                "FROM Huella h " +
                "JOIN h.idActividad a " +
                "JOIN a.idCategoria c " +
                "WHERE h.idUsuario = :uid " +
                "GROUP BY c.nombre";

        try (Session session = ConnectionDB.getInstance().getSession()) {
            Query<ImpactoCategoria> query = session.createQuery(hql, ImpactoCategoria.class);
            query.setParameter("uid", usuario);
            return query.list();
        }
    }

    /**
     * Obtiene el impacto total de TODOS los usuarios desglosado por categoría.
     */
    public List<ImpactoCategoria> obtenerImpactoGlobalPorCategoria() {
        String hql = "SELECT new com.example.footprinttracker.DTO.ImpactoCategoria(" +
                "   c.nombre, " +
                "   SUM(h.valor * c.factorEmision) " +
                ") " +
                "FROM Huella h " +
                "JOIN h.idActividad a " +
                "JOIN a.idCategoria c " +
                "GROUP BY c.nombre";

        try (Session session = ConnectionDB.getInstance().getSession()) {
            return session.createQuery(hql, ImpactoCategoria.class).list();
        }
    }

    /**
     * Cuenta usuarios únicos (Este no necesita DTO, devuelve un simple Long).
     */
    public Long contarUsuariosConHuellas() {
        String hql = "SELECT COUNT(DISTINCT h.idUsuario) FROM Huella h";
        try (Session session = ConnectionDB.getInstance().getSession()) {
            return session.createQuery(hql, Long.class).uniqueResult();
        }
    }

    /**
     * Calcula la media global de impacto por usuario en un mes específico.
     * Realiza una consulta de agregación que:
     * Filtra todas las huellas del mes y año indicados.</li>
     * Suma el impacto total (Valor * Factor de Emisión).</li>
     * <Cuenta cuántos usuarios únicos generaron esas huellas.</li>
     * Divide el total entre el número de usuarios para obtener la media per cápita.</li>
     *
     * @param mes El mes a consultar (1-12).
     * @param year El año a consultar.
     * @return El impacto medio (kg CO2) por usuario. Devuelve 0.0 si no hay registros.
     */
    public double mediaMensualGlobal(int mes, int year){
        String hql = "SELECT (SUM(h.valor * c.factorEmision) / COUNT(DISTINCT h.idUsuario)) " +
                "FROM Huella h "+
                "JOIN h.idActividad a " +
                "JOIN a.idCategoria c " +
                "WHERE month(h.fecha) = :mes " +
                "AND year(h.fecha) = :year";
        try (Session session = ConnectionDB.getInstance().getSession()) {
            Query<Double> query = session.createQuery(hql, Double.class);
            query.setParameter("mes", mes);
            query.setParameter("year", year);

            Double resultado = query.uniqueResult();
            return (resultado != null) ? resultado : 0.0;
        }catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }
}
