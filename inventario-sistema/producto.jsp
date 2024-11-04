<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema inventario</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.24/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/responsive/2.2.9/css/responsive.bootstrap4.min.css">
    <link rel="stylesheet" href="css/main.css?v=923" />
  </head>
  <body>
    <div id="blur-container"></div>
    <div id="login-modal" class="modal">
      <div class="modal-dialog">
        <div class="modal-content">
          <!-- Contenido del modal -->
          <div class="modal-header">
            <h5 class="modal-title">Inicio de Sesión</h5>
          </div>
          <div class="modal-body">
            <form>
              <div class="form-group">
                <label for="username">Usuario:</label>
                <input type="text" class="form-control" id="username" required>
              </div>
              <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" class="form-control" id="password" required>
              </div>
              <button type="button" id="sesion" class="btn btn-primary">Iniciar Sesión</button>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="main">
      <div class="navbar-side">
        <h6>
          <span class="icon">
            <i class="fas fa-warehouse"></i>
          </span>
          <span class="link-text">Panel inventario</span>
        </h6>
        <ul id="listaOpciones" style="display: none;">
          <li>
            <a href="#" class="myBtn" data-toggle="collapse" data-target="#my-sub" title="checadas" aria-expanded="false">
              <span class="icon">
                <i class="fas fa-list"></i>
              </span>
              <span class="link-text">Productos</span>
            </a>
            <div id="my-sub" class="collapse bg-secondary" data-permiso-id="2">
              <a href="#" title="registrar">
                <span class="icon">
                  <i class="fas fa-plus"></i>
                </span>
                <span class="link-text">Registrar</span>
              </a>
            </div>
            <div id="my-sub" class="collapse bg-secondary">
                <a href="#" title="salidas">
                  <span class="icon">
                    <i class="fas fa-exchange-alt"></i>

                  </span>
                  <span class="link-text">Movimientos</span>
                </a>
            </div>
            <div id="my-sub" class="collapse bg-secondary" data-permiso-id="7">
              <a href="#" title="historico">
                <span class="icon">
                  <i class="fas fa-file-alt"></i>

                </span>
                <span class="link-text">Histórico</span>
              </a>
            </div>
          </li>
        </ul>
      </div>
      <div class="content">
        <nav class="navbar navbar-dark bg-dark py-1">
          <a href="#" id="navBtn">
            <span id="changeIcon" class="fa fa-bars text-light"></span>
          </a>
          <div class="d-flex">
            <a class="nav-link text-light px-2" href="#" id="relojCabezeraContainer">
              <p style="margin: 0" id="relojCabezera"></p>
            </a>
            <a class="nav-link text-light px-2" href="#" id="internetConexion">
              <i class="fas fa-wifi"></i>
            </a>
          </div>
        </nav>
        <div class="container mt-4">
          <div id="inicio">
            <div class="row">
              <div class="col-md-6 mx-auto text-center">
                <h1 class="titulo-inicio">Bienvenido</h1>
                <p class="mt-3" id="subtitulo">Explora las funciones disponibles en tu sistema de inventario.</p>

              </div>
            </div>
          </div>
          <div id="salidas" style="display: none;">
            <div id="responsive-container" style="display: flex; align-items: center;">
                <div id="button-container" style="display: flex; align-items: center;">
                  <button style="display: none;" type="button" id="bajas" class="btn btn-dark" style="margin-right: 10px;"><i class='fas fa-trash'></i> Bajas</button>
                  <button style="display: none;" type="button" id="regresarActivos" class="btn btn-dark" style="margin-right: 10px;"><i class='fas fa-arrow-left'></i> Activos</button>
                </div>
            </div>
            <hr>
            <div id="animaciontabla" class="text-center">
              <h3>Cargando...</h3>
              <div class="spinner-border text-primary" role="status">
                <span class="sr-only">Cargando...</span>
              </div>
            </div>
            <div id="containerTable" class="table-responsive">
              <!-- Tabla para mostrar los datos -->
              <table id="tabla" class="table table-bordered table-striped mt-3 display nowrap" style="display: none; width: 100%">
                <thead class="thead-dark">
                  <tr></tr>
                </thead>
                <tbody>
                  <!-- Los datos se cargan dinámicamente aquí -->
                </tbody>
              </table>
            </div>
          </div>
          <div id="registrar" style="display: none;">
            <div class="card border-dark mb-3" style="max-width:100%;">
              <h3 class="card-header d-flex justify-content-between align-items-center dark-header">
                <span>Agregar Producto</span>
              </h3>
              <div class="card-body">
                <div class="row">
                  <div class="col-md-6" style="margin-top: 10px;">
                    <label for="nombre">Nombre:</label>
                    <input type="text" class="form-control" id="nombre" maxlength="100">
                  </div>
                  <div class="col-md-3" style="margin-top: 10px;">
                    <label for="precio">Precio:</label>
                    <input type="number" class="form-control" id="precio" maxlength="100">
                  </div>
                  <div class="col-md-3" style="margin-top: 10px;">
                    <label for="cantidad">Cantidad:</label>
                    <input type="number" class="form-control" id="cantidad" maxlength="100">
                  </div>
                </div>
              </div>
            </div>
            <hr>
            <div class="row justify-content-end">        
              <div class="col-md-3">
                <a id="btnRegistrarProducto" class="btn btn-outline-primary" style="width: 100%;">Registrar</a>
              </div>
            </div>
            <hr>
          </div>
          <div id="historico" style="display: none;">
            <hr>
            <div id="animaciontablaHistorico" class="text-center">
              <h3>Cargando...</h3>
              <div class="spinner-border text-primary" role="status">
                <span class="sr-only">Cargando...</span>
              </div>
            </div>
            <div id="containerTable" class="table-responsive">
              <!-- Tabla para mostrar los datos -->
              <table id="tablaHistorico" class="table table-bordered table-striped mt-3 display nowrap" style="display: none; width: 100%">
                <thead class="thead-dark">
                  <tr></tr>
                </thead>
                <thead id='filter-tablaHistorico' style='background-color: #4a4a4a; color: #ffffff; line-height: 25px;'>
                  <tr></tr>
                </thead>
                <tbody>
                  <!-- Los datos se cargan dinámicamente aquí -->
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
    </div>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.min.js"></script>
    <script src="https://cdn.datatables.net/1.10.24/js/jquery.dataTables.min.js"></script>
    <script src="https://cdn.datatables.net/1.10.24/js/dataTables.bootstrap4.min.js"></script>
    <script src="https://cdn.datatables.net/responsive/2.2.9/js/dataTables.responsive.min.js"></script>
    <script src="https://cdn.datatables.net/responsive/2.2.9/js/responsive.bootstrap4.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrious/4.0.2/qrious.min.js"></script>
    <script src="https://rawgit.com/eKoopmans/html2pdf/master/dist/html2pdf.bundle.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@10"></script>
    <script>
      var timestamp = new Date().getTime();
      var script = document.createElement('script');
      script.src = 'js/script.js?v=' + timestamp;
      document.head.appendChild(script);
    </script>
  </body>
</html>