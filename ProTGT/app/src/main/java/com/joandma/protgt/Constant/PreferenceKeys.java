package com.joandma.protgt.Constant;

/**
 * Created by Jorge Amores on 25/02/2018.
 */

public interface PreferenceKeys {
    //Constantes para atributos de usuario
    final String USER_TOKEN = "userToken";
    final String USER_NAME = "userNombre";
    final String USER_SURNAME = "userApellidos";
    final String USER_EMAIL = "userEmail";
    final String USER_PASSWORD = "userPassword";
    final String USER_PAIS = "userPais";
    final String USER_TELEFONO = "userTelefono";

    //Constantes para atributo de dirección
    final String ADDRESS_ID = "addressId";
    final String ADDRESS_PROVINCIA = "addressProvincia";
    final String ADDRESS_LOCALIDAD = "addressLocalidad";
    final String ADDRESS_CALLE = "addressCalle";
    final String ADDRESS_NUMERO = "addressNumero";
    final String ADDRESS_PISO = "addressPiso";
    final String ADDRESS_BLOQUE = "addressBloque";
    final String ADDRESS_PUERTA = "addressPuerta";

    //Constantes para atributo de contacto
    final String CONTACT_ID = "contacId";
    final String CONTACT_NAME = "contactName";
    final String CONTACT_TELEFONO = "contactTelefono";

    //Esto es para la comprobacion del boton de emergencia
    final String BOOLEAN_COMPROBACION = "comprobacion";

    //Constante para guardar localizacion
    final String LOCATION_LATLNG = "locLanLtd";

    //Constante para guardar api key de SMS Publi
    final String SMS_APIKEY = "a1fe84b7d2a873aef9c5a357b516468f";
}
