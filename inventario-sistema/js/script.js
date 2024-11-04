$(document).ready(function() {

    var selectedElement;

    $("#navBtn").click(function() {
        $(".main").toggleClass('animate');
    });

    $('.navbar-side ul li a').on('click', function() {
        
        // Verificar si el elemento <a> tiene la clase "myBtn"
        if ($(this).hasClass('myBtn')) {
            return; // No hacer nada si tiene la clase "myBtn"
        }
        // Verificar conexión a internet
        if (!internetConexion) {
            return; // No hacer nada si no hay internet
        }
        $('.navbar-side ul li a').removeClass('link-active');
        $(this).addClass('link-active');

        // Realiza acciones adicionales dependiendo del elemento seleccionado
        $("#" + selectedElement).fadeOut();
        selectedElement = $(this).attr('title');
        if ($("#inicio").is(":visible")) {
            $("#inicio").fadeOut();
        }
        if ($("#registrar").is(":visible")) {
            formularioProducto("clear");
        }
        if ($("#editar").is(":visible")) {
            $("#editar").fadeOut();
        }
        switch (selectedElement) {
            case 'registrar':
                $("#" + selectedElement).fadeIn();
                formularioProducto("clear");
                // obtenerUltimoId();

                break;
            case 'editar':
                $("#" + selectedElement).fadeIn();
                formularioProducto("clear");
                // obtenerUltimoId();

                break;
            case 'historico':

                $("#" + selectedElement).fadeIn();

                historico();

                break;
            case 'salidas':

                $("#" + selectedElement).fadeIn();

                activos();

                break;

        }
    });

    function verificarConexion() {
        const enlace = document.getElementById("internetConexion");
        const icono = enlace.querySelector("i");

        if (navigator.onLine) {

            internetConexion = true;
            icono.className = "fas fa-wifi";
        } else {

            internetConexion = false;
            icono.className = "fas fa-ban";
        }
    }

    // Verificar la conexión cada 3 segundos (puedes ajustar el intervalo según tus necesidades)
    setInterval(verificarConexion, 3000);

    function formularioProducto(accion) {

        var campos = {
            nombre: "Nombre",
            precio: "Precio",
            cantidad: "Cantidad",
        };

        switch (accion) {

            case "validate":
                
                var nombre = document.getElementById("nombre").value;
                var precio = document.getElementById("precio").value;
                var cantidad = document.getElementById("cantidad").value;

                for (var campo in campos) {

                    document.getElementById(campo).classList.remove("is-invalid");

                }

                var campoVacio = "";

                if (nombre === "" || precio === "" || cantidad === "") {
                    for (var campo in campos) {
                        if (document.getElementById(campo).value === "") {
                            campoVacio = campos[campo];
                            document.getElementById(campo).classList.add("is-invalid");
                            break;
                        }
                    }
                    return `El campo ${campoVacio} está vacío.`;
                }

                return 1;

            case "clear":

                for (var campo in campos) {

                    document.getElementById(campo).classList.remove("is-invalid");
                    $("#" + campo).val('');

                }
                
                
                break;

        }

    }

    $("#btnRegistrarProducto").click(function() {
        var resultadoValidacion = formularioProducto("validate");

        if (resultadoValidacion === 1) {
            registrarProducto();     
        } else {

            Swal.fire({
                icon: 'warning',
                text: resultadoValidacion,
                timer: 2000,
                showConfirmButton: false,
            });

        }

    });

    // Función para registrar productos
    function registrarProducto() {
        $.ajax({
            url: "http://localhost:8080/inventario-sistema/ProductoController",
            type: "POST",
            contentType: "application/x-www-form-urlencoded",
            data: {
                cmd: "registrar_producto",
                nombre: $("#nombre").val(),
                precio: $("#precio").val(),
                cantidad: $("#cantidad").val(),
            },
            success: function(data) {
                
                var message = data.message || data.error || "Operación completada"; // Mensaje por defecto
                var icon = data.error ? 'warning' : 'success'; // Cambia el icono según si hay un error
            
                Swal.fire({
                    icon: icon,
                    text: message,
                    timer: 3000, 
                    showConfirmButton: false
                }).then((result) => {
                    formularioProducto("clear");
                });
            },            
            error: function(xhr) {
                // Manejo de errores de la solicitud
                var errorMessage = xhr.responseText;
                Swal.fire({
                    icon: 'warning',
                    text: errorMessage,
                    timer: 2000,
                    showConfirmButton: false,
                });
                console.log(xhr);
            }
        });
    }
    
    
    function inicializarTabla(columnas, nombresColumnas, urlDatos, idTabla, cmdDatos, acciones, columnasConFiltro) {
        $("#animacion" + idTabla).show();
        $("#" + idTabla).fadeOut();
    
        // Comprobar si existe una instancia de DataTable y destruirla con el filtro
        if ($.fn.DataTable.isDataTable("#" + idTabla)) {
            $("#" + idTabla).DataTable().destroy();
            $('#filter-' + idTabla + ' th').remove();
        }
    
        // Crear el thead para los filtros
        var headerRow = "";
    
        // Agregar celdas al thead basado en el número de columnas
        for (var i = 0; i < columnas.length; i++) {
            headerRow += "<th></th>";
        }
    
        // Insertar el thead en la tabla
        $("#filter-" + idTabla).append(headerRow);
    
        $.ajax({
            url: "http://localhost:8080/inventario-sistema/ProductoController",
            method: 'POST',
            data: {
                cmd: cmdDatos
            },
            dataType: 'JSON',
            success: function(data) {
                $('#' + idTabla).DataTable({
                    "data": data,
                    "columns": generarColumnas(columnas, nombresColumnas, acciones),
                    "language": {
                        "url": "js/Spanish.json"
                    },
                    responsive: {
                        details: {
                            display: $.fn.dataTable.Responsive.display.childRowImmediate,
                            type: ''
                        },
                    },
                    initComplete: function () {
                        var table = this.api();
                        // Agregar selectores de filtro a la fila de filtros
                        table.columns().every(function () {
                            let column = this;
    
                            if (columnasConFiltro.includes(column.index())) {
                                // Crear elemento select
                                let select = document.createElement('select');
                                select.className = 'form-control';
                                
                                // Añadir la opción "Mostrar todos" directamente
                                select.add(new Option('Todos', ''));
    
                                // Agregar el select a la celda correspondiente en la fila de filtros
                                $('#filter-' + idTabla + ' th').eq(column.index()).append(select);
    
                                // Aplicar listener para cambios en el valor
                                select.addEventListener('change', function () {
                                    // Desactivar ordenamiento
                                    table.order([]).draw();
                                    column.search(select.value, { exact: true }).draw();
                                });
    
                                // Añadir lista de opciones
                                column.data().unique().sort().each(function (d, j) {
                                    select.add(new Option(d));
                                });
    
                                // Seleccionar por defecto el segundo elemento si la columna es la primera
                                if (column.index() === 0) {
                                    select.selectedIndex = 1; // Selecciona el segundo elemento por defecto
                                    select.dispatchEvent(new Event('change')); // Disparar el cambio para aplicar el filtro
                                }
                            }
                        });
                    }
                });
                $("#animacion" + idTabla).hide();
                $("#" + idTabla).fadeIn();
            },
            error: function(xhr) {
                // Parsear la respuesta JSON para obtener el mensaje de error
                var errorMessage = JSON.parse(xhr.responseText).error;
                Swal.fire({
                    icon: 'warning',
                    text: errorMessage,
                    timer: 2000,
                    showConfirmButton: false,
                });
                console.log(xhr);
            }
        });
    }
    
    function generarColumnas(columnas, nombresColumnas, acciones) {
        var columnasGeneradas = [];
    
        for (var i = 0; i < columnas.length; i++) {
            var columna = {
                "data": columnas[i],
                "title": nombresColumnas[i]
            };
    
            columnasGeneradas.push(columna);
        }

        if(acciones != ""){

            // Agregar la columna de acciones al final
            columnasGeneradas.push({
                "data": null,
                "title": "Acciones",
                "className": "dt-center",
                "render": function(data) {
                    if (acciones != "") {

                        return `<div class="btn-group-vertical">${acciones}</div>`;

                    }

                }
            });

        }
    
        return columnasGeneradas;
    }
    

    // Delegación de eventos para el botón "Dar salida" en el DataTable
    $('#tabla tbody').on('click', 'button#darSalida', function() {
        // Obtener los datos del producto de la fila actual
        var tabla = $('#tabla').DataTable();
        var data = tabla.row($(this).parents('tr')).data();
        var idProducto = data.idProducto;
        var nombre = data.nombre;
        var cantidadDisponible = data.cantidad; // Cantidad actual del producto

        // Mostrar un Swal con un campo de entrada para la cantidad a retirar
        Swal.fire({
            title: 'Dar salida de producto',
            text: `Producto: ${nombre} - Cantidad disponible: ${cantidadDisponible}`,
            icon: 'info',
            input: 'number',
            inputLabel: 'Cantidad a retirar',
            inputPlaceholder: 'Cantidad',
            inputAttributes: {
                min: 1,
                max: cantidadDisponible // Limitar al máximo de la cantidad disponible
            },
            showCancelButton: true,
            confirmButtonText: 'Retirar',
            cancelButtonText: 'Cancelar',
            preConfirm: (cantidadRetirar) => {
                return new Promise((resolve) => {
                    // Validar que la cantidad a retirar sea válida
                    if (cantidadRetirar > cantidadDisponible) {
                        Swal.showValidationMessage(
                            `No puedes retirar más de la cantidad disponible (${cantidadDisponible})`
                        );
                    } else if (cantidadRetirar <= 0) {
                        Swal.showValidationMessage(
                            'La cantidad debe ser mayor a 0'
                        );
                    } else {
                        resolve(cantidadRetirar); // Pasar la cantidad a retirar si es válida
                    }
                });
            }
        }).then((result) => {
            if (result.isConfirmed) {
                // La cantidad a agregar es válida y ha sido confirmada
                var cantidadAgregar = result.value;
                var nuevaCantidad = cantidadDisponible - parseInt(cantidadAgregar);
    
                // Guardar referencia al botón
                var button = $(this);
    
                $.ajax({
                    url: "http://localhost:8080/inventario-sistema/ProductoController",
                    type: "POST",
                    contentType: "application/x-www-form-urlencoded",
                    data: {
                        cmd: "registrar_salida",
                        idProducto: idProducto,
                        cantidad: cantidadAgregar,
                        idUsuario: idUsuario
                    },
                    success: function(x) {
                        // Actualizar la cantidad en el objeto de datos de la fila
                        data.cantidad = nuevaCantidad;
    
                        // Redibujar la fila con la nueva cantidad
                        tabla.row(button.parents('tr')).data(data).draw();
    
                        var message = x.message || x.error || "Operación completada"; // Mensaje por defecto
                        var icon = x.error ? 'warning' : 'success'; // Cambia el icono según si hay un error
                    
                        Swal.fire({
                            icon: icon,
                            text: message,
                            timer: 3000, 
                            showConfirmButton: false
                        }).then((result) => {
                            formularioProducto("clear");
                        });
                    },
                    error: function(xhr) {
                        // Manejo de errores de la solicitud
                        var errorMessage = xhr.responseText;
                        Swal.fire({
                            icon: 'warning',
                            text: errorMessage,
                            timer: 2000,
                            showConfirmButton: false,
                        });
                        console.log(xhr);
                    }
                });
                
            }
        });
    });

    // Delegación de eventos para el botón "Dar entrada" en el DataTable
    $('#tabla tbody').on('click', 'button#darEntrada', function() {
        // Obtener los datos del producto de la fila actual
        var tabla = $('#tabla').DataTable();
        var data = tabla.row($(this).parents('tr')).data();
        var idProducto = data.idProducto;
        var nombre = data.nombre;
        var cantidadDisponible = data.cantidad; // Cantidad actual del producto
    
        // Mostrar un Swal con un campo de entrada para la cantidad a agregar
        Swal.fire({
            title: 'Dar entrada de producto',
            text: `Producto: ${nombre} - Cantidad actual: ${cantidadDisponible}`,
            icon: 'info',
            input: 'number',
            inputLabel: 'Cantidad a agregar',
            inputPlaceholder: 'Cantidad',
            inputAttributes: {
                min: 1
            },
            showCancelButton: true,
            confirmButtonText: 'Agregar',
            cancelButtonText: 'Cancelar',
            preConfirm: (cantidadAgregar) => {
                return new Promise((resolve) => {
                    // Validar que la cantidad a agregar no sea negativa ni reduzca el inventario
                    if (cantidadAgregar <= 0) {
                        Swal.showValidationMessage(
                            'La cantidad debe ser mayor a 0'
                        );
                    } else {
                        resolve(cantidadAgregar); // Pasar la cantidad a agregar si es válida
                    }
                });
            }
        }).then((result) => {
            if (result.isConfirmed) {
                // La cantidad a agregar es válida y ha sido confirmada
                var cantidadAgregar = result.value;
                var nuevaCantidad = cantidadDisponible + parseInt(cantidadAgregar);
    
                // Guardar referencia al botón
                var button = $(this);
    
                $.ajax({
                    url: "http://localhost:8080/inventario-sistema/ProductoController",
                    type: "POST",
                    contentType: "application/x-www-form-urlencoded",
                    data: {
                        cmd: "registrar_entrada",
                        idProducto: idProducto,
                        cantidad: cantidadAgregar,
                        idUsuario: idUsuario
                    },
                    success: function(x) {
                        // Actualizar la cantidad en el objeto de datos de la fila
                        data.cantidad = nuevaCantidad;
    
                        // Redibujar la fila con la nueva cantidad
                        tabla.row(button.parents('tr')).data(data).draw();
    
                        var message = x.message || x.error || "Operación completada"; // Mensaje por defecto
                        var icon = x.error ? 'warning' : 'success'; // Cambia el icono según si hay un error
                    
                        Swal.fire({
                            icon: icon,
                            text: message,
                            timer: 3000, 
                            showConfirmButton: false
                        }).then((result) => {
                            formularioProducto("clear");
                        });
                    },
                    error: function(xhr) {
                        // Manejo de errores de la solicitud
                        var errorMessage = xhr.responseText;
                        Swal.fire({
                            icon: 'warning',
                            text: errorMessage,
                            timer: 2000,
                            showConfirmButton: false,
                        });
                        console.log(xhr);
                    }
                });
            }
        });
    });
    

    $("#blur-container").fadeIn();
    $("#login-modal").fadeIn();

    $(document).on('click', '#sesion', sesion_admin);

    // Define la variable accionesMovimientos
    var accionesMovimientos = "";
    var idUsuario;
    var bajasMostrar = false;

    function sesion_admin() {
        $.ajax({
            url: "http://localhost:8080/inventario-sistema/ProductoController",
            type: "POST",
            contentType: "application/x-www-form-urlencoded",
            data: {
                cmd: "iniciar_sesion",
                username: $("#username").val(),
                password: $("#password").val()
            },
            success: function(data) {
                if (data.success) {
                    $("#blur-container").fadeOut();
                    $("#login-modal").fadeOut();
                    $("#listaOpciones").show();
                    $("#bajas").hide(); 

                    idUsuario = data.idUsuario;

                    // Crear un array con los IDs de los permisos del usuario
                    var permisosUsuario = data.permisos.map(function(permiso) {
                        return permiso.permisoId;
                    });

                    // Ocultar divs que el usuario no tiene permiso para ver
                    $("div[data-permiso-id]").each(function() {
                        var permisoId = parseInt($(this).attr("data-permiso-id"), 10);
                        if (!permisosUsuario.includes(permisoId)) {
                            $(this).hide();
                        }
                    });

                    // Bucle para agregar botones a accionesMovimientos según los permisos
                    data.permisos.forEach(function(permiso) {
                        var boton = ""; // Variable temporal para el botón
                        switch (permiso.permisoId) {
                            case 5:
                                boton = "<button id='darSalida' class='btn btn-outline-primary'> <i class='fas fa-sign-out-alt'></i> Dar salida</button>";
                                break;
                            case 3:
                                boton = "<button id='darEntrada' class='btn btn-outline-primary'> <i class='fas fa-door-open'></i> Dar entrada</button>";
                                break;
                            case 4:
                                boton = "<button id='darDebaja' class='btn btn-outline-danger'> <i class='fas fa-trash'></i> Dar de baja</button>";
                                bajasMostrar = true;
                                break;  
                            default:
                                break;
    
                        }
                        // Agregar el botón a accionesMovimientos si se generó uno
                        if (boton) {
                            accionesMovimientos += boton;
                        }
                    });
                    
                } else {
                    Swal.fire({
                        icon: 'warning',
                        text: data.error || "Error desconocido",
                        timer: 2000,
                        showConfirmButton: false,
                    });
                }
            },
            error: function(xhr) {
                Swal.fire({
                    icon: 'warning',
                    text: "Error en la solicitud al servidor",
                    timer: 2000,
                    showConfirmButton: false,
                });
                console.log(xhr);
            }
        });
    }

    $('#tabla tbody').on('click', 'button#darDebaja', function() {
        // Obtener los datos del producto de la fila actual
        var tabla = $('#tabla').DataTable();
        var rowElement = $(this).parents('tr'); // Guardar la referencia a la fila
        var data = tabla.row(rowElement).data();
        var idProducto = data.idProducto;
        var nombre = data.nombre;
    
        Swal.fire({
            icon: 'warning',
            text: `¿Dar de baja producto: ${nombre}?`,
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Aceptar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: "http://localhost:8080/inventario-sistema/ProductoController",
                    type: "POST",
                    contentType: "application/x-www-form-urlencoded",
                    data: {
                        cmd: "dar_de_baja",
                        idProducto: idProducto,
                    },
                    success: function(data) {
                        var message = data.message || data.error || "Operación completada"; // Mensaje por defecto
                        var icon = data.error ? 'warning' : 'success'; // Cambia el icono según si hay un error
    
                        // Usa la referencia rowElement para eliminar la fila 
                        tabla.row(rowElement).remove().draw(false);
    
                        Swal.fire({
                            icon: icon,
                            text: message,
                            timer: 3000, 
                            showConfirmButton: false
                        }).then((result) => {
                            formularioProducto("clear");
                        });
                    },            
                    error: function(xhr) {
                        // Manejo de errores de la solicitud
                        var errorMessage = xhr.responseText;
                        Swal.fire({
                            icon: 'warning',
                            text: errorMessage,
                            timer: 2000,
                            showConfirmButton: false,
                        });
                        console.log(xhr);
                    }
                });
            }
        });
    });

    $('#tabla tbody').on('click', 'button#reactivarProducto', function() {
        // Obtener los datos del producto de la fila actual
        var tabla = $('#tabla').DataTable();
        var rowElement = $(this).parents('tr'); // Guardar la referencia a la fila
        var data = tabla.row(rowElement).data();
        var idProducto = data.idProducto;
        var nombre = data.nombre;
    
        Swal.fire({
            icon: 'warning',
            text: `¿Reactivar producto: ${nombre}?`,
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Aceptar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: "http://localhost:8080/inventario-sistema/ProductoController",
                    type: "POST",
                    contentType: "application/x-www-form-urlencoded",
                    data: {
                        cmd: "reactivar_producto",
                        idProducto: idProducto,
                    },
                    success: function(data) {
                        var message = data.message || data.error || "Operación completada"; // Mensaje por defecto
                        var icon = data.error ? 'warning' : 'success'; // Cambia el icono según si hay un error
    
                        // Usa la referencia rowElement para eliminar la fila 
                        tabla.row(rowElement).remove().draw(false);
    
                        Swal.fire({
                            icon: icon,
                            text: message,
                            timer: 3000, 
                            showConfirmButton: false
                        }).then((result) => {
                            formularioProducto("clear");
                        });
                    },            
                    error: function(xhr) {
                        // Manejo de errores de la solicitud
                        var errorMessage = xhr.responseText;
                        Swal.fire({
                            icon: 'warning',
                            text: errorMessage,
                            timer: 2000,
                            showConfirmButton: false,
                        });
                        console.log(xhr);
                    }
                });
            }
        });
    });
    
    $(document).on('click', '#bajas', bajas);

    function bajas(){

        var columnas = ["nombre", "precio", "cantidad"];
        var nombresColumnas = ["Producto", "Precio", "Cantidad"];
        var accionesBajas = `<button id="reactivarProducto" class="btn btn-outline-success">
                            <i class="fas fa-check"></i> Reactivar
                        </button>`;
        var urlDatos = "xxxx";
        idTabla = 'tabla';
        cmdDatos = 'obtener_inactivos';

        $("#bajas").hide();

        $("#regresarActivos").show();

        inicializarTabla(columnas, nombresColumnas, urlDatos, idTabla, cmdDatos, accionesBajas, [1, 2, 3]);

    }

    $(document).on('click', '#regresarActivos', activos);

    function activos(){

        var columnas = ["nombre", "precio", "cantidad"];
        var nombresColumnas = ["Producto", "Precio", "Cantidad"];
        var urlDatos = "xxxx";
        idTabla = 'tabla';
        cmdDatos = 'obtener_salidas';

        if(bajasMostrar){

            $("#bajas").show();

        }

        $("#regresarActivos").hide();

        inicializarTabla(columnas, nombresColumnas, urlDatos, idTabla, cmdDatos, accionesMovimientos, [1, 2, 3]);

    }

    function historico(){

        var columnas = ["tipoMovimiento", "nombreProducto", "nombreUsuario", "fecha", "cantidad"];
        var nombresColumnas = ["Tipo movimiento", "Producto", "Usuario", "Fecha y hora", "Cantidad"];
        var urlDatos = "xxxx";
        var acciones = "";
        idTabla = 'tablaHistorico';
        cmdDatos = 'obtener_movimientos_inventario';

        inicializarTabla(columnas, nombresColumnas, urlDatos, idTabla, cmdDatos, acciones, [0]);

    }

});
