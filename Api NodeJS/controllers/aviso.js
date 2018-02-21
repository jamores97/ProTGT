const mongoose = require('mongoose');
const service = require('../services');
const Aviso = require('../models/aviso');
const User = require('../models/user');

// POST Registrar un aviso
module.exports.addAviso = (req, res) => {

    User.findOne({_id: req.user}, (err, user) => {
        if (err) return res.status(401).jsonp({
            error: 401,
            mensaje: `Error de autentificación`
        });

        if (!user) return res.status(404).jsonp({
            error: 404,
            mensaje: `No se encuentra el usuario`
        });

        let aviso = new Aviso({
            localizacion: req.body.localizacion,
            user: mongoose.Types.ObjectId(user._id),
            estado: 0
        });

        aviso.save(function (err, result) {
            if (err) return res.status(500).jsonp({
                error: 500,
                mensaje: `${err.message}`
            });

            res.status(201).jsonp(result);
        });
    });
};

// GET mostrar todos los aviso
module.exports.showAllAvisos = (req, res) => {

    Aviso.find((err, avisos) => {
        if (err) return res.status(401).jsonp({
            error: 401,
            mensaje: `Error de autentificación`
        });
        Aviso.populate(avisos, {path: "user", select: '_id nombre apellidos email pais telefono domicilio'}, (err, avisos) => {
            if (err) return res.status(500).jsonp({
                error: 500,
                mensaje: `${err.message}`
            });
            res.status(200).jsonp(avisos);
        });
    });
};

// GET mostrar todos los aviso sin resolver
module.exports.showAllAvisosSinResolver = (req, res) => {

    Aviso.find({estado: 0},(err, avisos) => {
        if (err) return res.status(401).jsonp({
            error: 401,
            mensaje: `Error de autentificación`
        });

        Aviso.populate(avisos, {path: "user", select: '_id nombre apellidos email pais telefono domicilio'}, (err, avisos) => {
            if (err) return res.status(500).jsonp({
                error: 500,
                mensaje: `${err.message}`
            });
            res.status(200).jsonp(avisos);
        });
    });
};

// GET Mostrar detalles de un aviso
module.exports.showOneAviso = (req, res) => {

    Aviso.findById({_id: req.params.id_aviso}, (err, aviso) =>{
        if (err) return res.status(401).jsonp({
            error: 401,
            mensaje: `Error de autentificación`
        });

        if (!aviso) return res.status(404).jsonp({
            error: 404,
            mensaje: `No se encuentra el aviso`
        });

        Aviso.populate(aviso, {path: "user", select: '_id nombre apellidos email pais telefono domicilio'}, (err, aviso) => {
            if (err) return res.status(500).jsonp({
                error: 500,
                mensaje: `${err.message}`
            });
            res.status(200).jsonp(aviso);

        });
    });
};

//PUT Cambiar estado aviso
module.exports.changeStatusAviso = (req, res) => {
    Aviso.findById({_id: req.params.id_aviso}, (err, aviso) => {
        if (err) return res.status(401).jsonp({
            error: 401,
            mensaje: `Error de autentificación`
        });

        if (!aviso) return res.status(404).jsonp({
            error: 404,
            mensaje: `No se encuentra el aviso`
        });

        aviso.estado = req.body.estado;

        aviso.save(function (err, result) {
            if (err) return res.status(500).jsonp({
                error: 500,
                mensaje: `${err.message}`
            });

            res.status(201).jsonp(result);
        });
    });
};