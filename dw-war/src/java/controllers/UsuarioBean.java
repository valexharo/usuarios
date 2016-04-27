/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import dwEntities.Usuario;
import dwManagers.UsuarioSessionLocal;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.primefaces.event.RowEditEvent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author root
 */
@Named(value = "usuarioBean")
@Stateless
public class UsuarioBean implements Serializable {
    //private final HttpServletRequest httpServletRequest;
   // private final FacesContext faceContext;
   // private FacesMessage facesMessage;
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
     @EJB
    private UsuarioSessionLocal usuarioSession;
    private List<Usuario>items = null;
    private Usuario currentItem;
    /**
     * Creates a new instance of ModuloBean
     */
    public UsuarioBean() {
      /*  faceContext=FacesContext.getCurrentInstance();
        httpServletRequest=(HttpServletRequest)faceContext.getExternalContext().getRequest();*/
     /*   if(httpServletRequest.getSession().getAttribute("sessionUsuario")!=null)
        {
            usuarioSession=httpServletRequest.getSession().getAttribute("sessionUsuario").toString();
        }*/
    }
    
   /* public String login()
    {
        if(usuario.equals("KAAF") && contrasenia.equals("030191"))
        {
            httpServletRequest.getSession().setAttribute("sessionUsuario", usuario);
            facesMessage=new FacesMessage(FacesMessage.SEVERITY_INFO, "Acceso Correcto", null);
            faceContext.addMessage(null, facesMessage);
            return "acceso";
        }
        else
        {
            facesMessage=new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario o contraseña incorrecto", null);
            faceContext.addMessage(null, facesMessage);
            return "index";
        }
    }*/
    
  /*  public String cerrarSession()
    {
        httpServletRequest.getSession().removeAttribute("sessionUsuario");
        facesMessage=new FacesMessage(FacesMessage.SEVERITY_INFO, "Session cerrada correctamente", null);
        faceContext.addMessage(null, facesMessage);
        return "index";
    }*/
    
    //<editor-fold defaultstate="collapsed" desc="GET - SET methods">
    public List<Usuario>getItems(){
        if(items==null){
            items = usuarioSession.obtainAll();
        }
        return items;
    }
    
    public void setItems(List<Usuario>items){
        this.items = items;
    }

    public Usuario getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(Usuario currentItem) {
        this.currentItem = currentItem;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Prepare view methods">
    private void recreateList(){
        items = null;
    }
    
    public String prepareList(){
        recreateList();
        return "usuario?faces-redirect=true";
    }
    
    public void prepareNewItem(){
        currentItem = new Usuario();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="CRUD methods">
    
    //Create new item method
    public void createItem() {
        Date hoy = new Date();
        
        try {   
            Long id = usuarioSession.getNextIdCampo();
            currentItem.setIdUsuario(id);
            currentItem.setEstado('1');
            currentItem.setFechaCreacion(hoy);
            usuarioSession.create(currentItem);
            recreateList();            
            addInfoMessage("Creado exitosamente", "");
        } catch (Exception e) {
            String lastCause = getLastCause(e);
            if (lastCause.contains("Entrada duplicada")) {//Personalised message for unique key error
                addErrorMessage("Un error ha ocurrido durante la creación", "Registro duplicado: Usuario ya existe.");
            } else {
                addErrorMessage("Un error ha ocurrido durante la creación", lastCause);
            }                     
        }
    }
    
    //Listener method for items table when editing one row
    public void onRowEdit(RowEditEvent event) {
        Usuario selectedItem = (Usuario) event.getObject();
        try {            
            usuarioSession.update(selectedItem);                        
            addInfoMessage("Actualizado exitosamente", "");       
        } catch (Exception e) {
            addErrorMessage("Un error ha ocurrido mientras se actualizaba el registro", getLastCause(e));
            
        }
    }
    
    //Delete item method when selected on items table       
    public void deleteItem() {         
        try {            
            usuarioSession.remove(currentItem);
            recreateList();
            addInfoMessage("Eliminado exitosamente", "");            
        } catch (Exception e) {            
            addErrorMessage("Un error ha ocurrido mientra se eliminaba el registro", getLastCause(e));            
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Message methods">
    //Method to add Information Message on page
    private void addInfoMessage(String a_msg, String a_msgDetail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, a_msg, a_msgDetail));
    }

    //Method to add Error Message on page
    private void addErrorMessage(String a_msg, String a_msgDetail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, a_msg, a_msgDetail));
    }

    //Recursive Method to obtain description of last error cause on error stack
    private String getLastCause(Throwable a_ex) {
        Throwable cause = a_ex;
        if (a_ex.getCause() == null) {
            return cause.toString();
        } else {
            return getLastCause(a_ex.getCause());
        }
    }
    //</editor-fold>
}
