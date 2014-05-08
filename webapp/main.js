// vim: et sw=2 ts=2 sts=2

(function(){
  var width = 1000, height = 500;
  var room = location.hash.slice(1);

  // full screen button
  var page = $("jwar")[0];
  $(".fullscreen-btn").click(function () {
    if (screenfull.enabled)
      screenfull.toggle(page);
    return false;
  });

  var map = d3.select(".map");
  var screen = d3.select(".map-screen");
  var svg = map.append("svg")
    .attr("viewBox", "0 0 " + width + " " + height)
    .attr("preserveAspectRatio", "xMinYMin meet");
    //.attr("width", "100%")
    //.attr("height", height);
  window.svg = svg;//DEBUG
  var defs = svg.append("defs")
  var current_player = d3.select(".current-player");
  var current_state = d3.select(".current-state");
  var hovered_country = d3.select(".hovered-country");

  //var filter = defs.append("filter").attr("id", "shadow");
  //filter.append("feMorphology")
  //  .attr("in", "SourceGraphic")
  //  .attr("radius", 1.0)
  //  .attr("operator", "dilate")
  //  .attr("result", "dilated");
  //filter.append("feColorMatrix")
  //  .attr("in", "dilated")
  //  .attr("result", "textbg")
  //  .attr("type", "matrix")
  //  .attr("values",
  //    "-0.4 -0.3 -0.3 0   1   " +
  //    "-0.3 -0.4 -0.3 0   1   " +
  //    "-0.3 -0.3 -0.4 0   1   " +
  //    " 0    0    0   1   0   ");
  //var merge = filter.append("feMerge")
  //merge.append("feMergeNode").attr("in", "textbg");
  //merge.append("feMergeNode").attr("in", "SourceGraphic");

  // Possible projections
  var projections = [
    //0
    d3.geo.kavrayskiy7()
      .scale(200),
    d3.geo.eckert4()
      .scale(200)
      .translate([width / 2, height / 2]),
    d3.geo.cylindricalStereographic()
      .parallel(45)
      .scale(230)
      .translate([width / 2, height / 2 + 50]),
    d3.geo.miller()
      .scale(180)
      .translate([width / 2, height / 2 + 50]),
    //4
    d3.geo.equirectangular()
      .scale(160)
      .translate([width / 2, height / 2])
      .precision(.1),
    d3.geo.naturalEarth()
      .scale(220)
      .translate([width / 2, height / 2 + 30])
      .precision(.1),
    d3.geo.lagrange()
      .scale(260)
      .translate([width / 2, height / 2 + 50])
      .precision(.1),
    d3.geo.orthographic()
      .scale(250)
      .translate([width / 2, height / 2])
      .clipAngle(92),
    //8
    d3.geo.stereographic()
      .scale(245)
      .translate([width / 2, height / 2])
      .rotate([-20, 0])
      .clipAngle(180 - 1e-4)
      .clipExtent([[0, 0], [width, height]])
      .precision(.1)
  ]

  var path, transform, projection;

  function updateMap() {
    svg.selectAll(".country,.graticule").attr("d", path);
    svg.selectAll(".country-label").attr("transform", transform);
    svg.selectAll(".country-circle")
      .attr("transform", circleTransform)
      .attr("class", function(d) { return "country-circle" + (path(d) ? "" : " hide");});
  }

  function setupProjection(p) {
    projection = p;
    var color = d3.scale.category20();
    path = d3.geo.path().projection(projection);
    //var transform = function(d) { return "translate(" + path.centroid(d) + ")"; }
    transform = function(d) { return "translate(" + projection(d.geometry.coordinates) + ")"; }
    updateMap();
  }

  var graticule = d3.geo.graticule();
  svg.append("path")
    .datum(graticule)
    .attr("class", "graticule");
  svg.append("path")
    .datum(graticule.outline)
    .attr("class", "outline");

  // helper functions to transform
  // screen positions on rotation coordinates

  var λ = d3.scale.linear()
    .domain([0, width])
    .range([-180, 180]);

  var φ = d3.scale.linear()
    .domain([0, height])
    .range([90, -90]);

  // the drag

  map.on("click", function(d,i) {
    //if (d3.event.defaultPrevented) return; // click suppressed
  });

  var _lr, _ortho = false;
  $(".globe-btn").click(function() {
    //mapdrag.translate(0, 0, 0);//FIXME: reset draggin
    if (_ortho)
      setupProjection(projections[4]);
    else
      setupProjection(projections[7]);
    _ortho = !_ortho;
    return false;
  });
  var mapdrag = d3.behavior.drag()
    // remember rotation
    .on("dragstart", function() {
      var m = d3.mouse(this);
      var p = projection.rotate();
      _lr = [p[0] - λ(m[0]) , p[1] - φ(m[1])];
      // optimizations
      //svg.selectAll(".country-label").classed("moving", true);
    })
    // do a delta rotation
    .on("drag", function() {
      var r, m = d3.mouse(this);
      if (_ortho)
        r = [λ(m[0]) + _lr[0], φ(m[1]) + _lr[1]];
      else
        r = [λ(m[0]) + _lr[0], 0];
      projection.rotate(r);
      updateMap();
    })
    // clean up optimizations
    .on("dragend", function() {
      //svg.selectAll(".country-label").classed("moving", false);
    });

  map.call(mapdrag);

  // load the projections
  setupProjection(projections[4]);

  var packsize = 40;
  var pack = d3.layout.pack().size([packsize, packsize]);
  function armyList(armies) {
    var l = {children:[]};
    while (armies > 5) {
      l.children.push({value: 5});
      armies -= 5;
    }
    while (armies > 0) {
      l.children.push({value: 1});
      armies--;
    }
    window.nodes = pack.nodes(l);
    return pack.nodes(l).filter(function(d) { return !d.children; });
  }

  var cont_sizes = {
    AF: 6,
    AS: 12,
    AU: 4,
    EU: 7,
    NA: 9,
    SA: 4,
    AN: 1
  };

  var game_state;
  var country_state = {};

  function circleTransform(d) {
    return transform(d) + " rotate(" + (d.rotation = (d.rotation || (Math.random() * 360))) + ")";
  }

  function loadMap(mapfile) {
    d3.json(mapfile, function(error, world) {
      var countries = topojson.feature(world, world.objects.countries).features,
      neighbors = topojson.neighbors(world.objects.countries.geometries),
      labels = topojson.feature(world, world.objects.labels).features;
      window.world = world;

      svg.selectAll(".country")
        .data(countries)
        .enter().insert("path", ".graticule")
        .attr("class", function(d, i) { return ["country", d.properties.CONT , "c" + (d.id % cont_sizes[d.properties.CONT])].join(" "); })
        .attr("data-country", function(d) { return d.properties.CC })
        .attr("d", path);
        //.style("fill", function(d, i) { return color(d.color = d3.max(neighbors[i], function(n) { return countries[n].color; }) + 1 | 0); });

      //svg.selectAll(".country-circle")
      //  .data(labels)
      //  .enter().append("circle")
      //  .attr("class", "country-circle")
      //  .attr("id", function(d) { return d.properties.CC })
      //  .attr("transform", transform)
      //  .attr("r", "9")
      //  .attr("data-country", function(d) { return d.properties.CC })

      svg.selectAll(".country-circle")
        .data(labels)
        .enter().append("g")
        .attr("class", function(d) { return "country-circle" + (path(d) ? "" : " hide");})
        .attr("id", function(d) { return d.properties.CC })
        //.attr("transform", transform)
        //.attr("transform", function(d) { return "rotate(25) " + transform(d); })
        .attr("transform", circleTransform)
        .attr("data-country", function(d) { return d.properties.CC })

      svg.selectAll(".country-label")
        .data(labels)
        .enter().append("text")
        .attr("class", "country-label")
        .attr("id", function(d) { return d.properties.CC })
        .attr("transform", transform)
        .attr("dy", ".35em")
        .attr("data-country", function(d) { return d.properties.CC })
        ;//.text(function(d) { return d.properties.NAME; });

      function textForCountry(prop) {
          var cs = country_state[prop.CC];
          return prop.NAME + (cs ? " <span class=\"badge\">" + cs.exercitos + "</span>" : "");
      }

      svg.selectAll(".country,.country-label,.country-circle")
        .on("mouseover", function(d) {
          hovered_country
          //.html(d.properties.CC + ": " + d.properties.NAME);
          .html(textForCountry(d.properties));
        });

      var pais_selecionado;
      svg.selectAll(".country,.country-label,.country-circle")
        .on("click", function(d) {
          switch (game_state) {
            case "DISTRIBUICAO_INICIAL":
            case "REFORCANDO_TERRITORIOS":
              sendCommand("reforcar " + d.properties.CC + " 1");
              break;
            case "ESCOLHENDO_ATAQUE":
              if (pais_selecionado == null) {
                pais_selecionado = d.properties.CC;
                $(".selected-country .country").html(textForCountry(d.properties));
              } else {
                sendCommand("atacar " + pais_selecionado + " " + d.properties.CC);
                $(".selected-country .country").html("");
                pais_selecionado = null;
              }
              break;
            case "DESLOCAR_EXERCITOS":
              if (pais_selecionado == null) {
                pais_selecionado = d.properties.CC;
                $(".selected-country .country").html(textForCountry(d.properties));
              } else {
                sendCommand("deslocar " + pais_selecionado + " " + d.properties.CC);
                $(".selected-country .country").html("");
                pais_selecionado = null;
              }
              break;
            default:
              $(".command .cmd").val(($(".command .cmd").val() || "") + " " + d.properties.CC);
              break;
          }
        });
    });
    screen.classed("hide", false);
  }

  $(".occupy .btn").click(function () {
    sendCommand("ocupar " + $(this).data("armies"));
  });

  $(".ok").click(function() {
    sendCommand("pronto");
  });

  $(".current-player").popover();

  // API communication
  var _ns = 'br.eb.ime.jwar.webapi.';
  var user = 'Usuário ' + Math.floor((Math.random()*1000)+1);
  console.log(user);
  var chatEndpoint = '/chat';
  var apiEndpoint = '/api';
  var chatSocket =  io.connect(chatEndpoint);
  var apiSocket = io.connect(apiEndpoint);

  chatSocket.on('message', function (data) {
    switch (data.type) {
    case "MESSAGE":
      var chat_log = $('<div>').addClass("chat-log");
      chat_log.append($("<span>").addClass("chat-user").text(data.user));
      chat_log.append($("<span>").addClass("chat-message").text(data.message))
      $('.chat .console').prepend(chat_log);
      break;
    case "JOIN":
      //TODO
      break;
    case "LEAVE":
      //TODO
      break;
    }
  });

  chatSocket.on('connect', function () {
    chatSocket.json.send({
      '@class': _ns + 'ChatObject',
      user: user,
      message: "Hello World!",
      type: "JOIN",
      room: room
    });
  });

  window.onhashchange = function() {
    //sendCommand("Hello World!", "JOIN");
    //room = location.hash.slice(1);
    location.reload();//hack while there is not a better solution
  }

  apiSocket.on('connect', function () {
    console.log("Connected to the server, loading map...");
    sendCommand("Hello World!", "JOIN");
  });

  apiSocket.on('disconnect', function () {
    console.log("Disconnected from server!");
    retries = 10;
    mapfile = null;
  });

  apiSocket.on('message', function (data) {
    console.log(data);//DEBUG

    switch (data.type) {

    case "CREATE":
      roomHash = "#" + data.room;
      roomLink = location.protocol + "//" + location.host + location.pathname + roomHash;
      $(".room-link").attr("href", roomHash).text(roomLink);
      break;

    case "COMMAND":
      var cmd_log = $('<div>');
      cmd_log.prepend($("<pre>").addClass("console-log").text(data.output));
      $('.command .console').prepend(cmd_log);
      break;

    case "STATE":

      if (data.vencedor) {
        $('#winModal .winner').text(data.vencedor.nome);
        $('#winModal .mission').text(data.vencedor.objetivo.description);
        $('#winModal').modal('show');
      } else {
        $('#winModal').modal('hide');
      }

      current_player
        .attr("class", current_player.attr("class").replace(/\bowner-.*\b/) + " owner-" + data.atual.cor.toLowerCase())
        .attr("data-content", data.atual.objetivo.description)
        .text(data.atual.nome);

      game_state = data.estado;
      current_state
        .text(data.estado);

      $(".armies").text(data.reforcos || "");

      $(".occupy").hide();
      $(".ok").hide();
      $(".selected-country").hide();
      switch (game_state) {
        case "OCUPANDO_TERRITORIO":
          $(".occupy").show();
          break;
        case "ESCOLHENDO_ATAQUE":
        case "DESLOCAR_EXERCITOS":
          $(".selected-country").show();
          $(".ok").show();
          break;
      }

      // load the map if recently connected
      if (data.welcome) {
        loadMap(data.mapfile);
        $(".welcome").hide();
      }

      // update the state
      setTimeout(function() {
        data.tabuleiro.paises.forEach(function (pais) {
          country_state[pais.codigo] = pais;
          var colorclass = "owner-" + (pais.dono.cor || "").toLowerCase();
          var circles = svg.select("#" + pais.codigo + ".country-circle")
            .selectAll("circle")
            .data(armyList(pais.exercitos + (data.movidos ? (data.movidos[pais.nome] || 0) : 0)))
            .attr("r", function(d) { return d.value + 3.5; })
            .attr("cx", function(d) { return d.x - packsize / 2; })
            .attr("cy", function(d) { return d.y - packsize / 2; })
            .attr("class", colorclass)
          circles.enter()
            .append("circle")
            .attr("r", function(d) { return d.value + 3.5; })
            .attr("cx", function(d) { return d.x - packsize / 2; })
            .attr("cy", function(d) { return d.y - packsize / 2; })
            .attr("class", colorclass)
          circles.exit()
            .remove()
          //d3.select("#" + pais.codigo + ".country-label")
            //.text(pais.exercitos);
            //.text(pais.nome + " [" + pais.exercitos + "]");
        });
      }, 500); // hack to wait for countries to load
      break;
    }
  });

  window.sendCommand = function sendCommand(command, type) {
    apiSocket.json.send({
        '@class': _ns + 'CommandObject',
        command: command,
        type: type || "COMMAND",
        room: room
    });
  }

  $(".chat-form").on('submit', function (e) {
    e.preventDefault();

    // get the message and clear the form
    var message = $('.chat-form .msg').val();
    $('.chat-form .msg').val('');

    //console.log(user + ": " + message);
    chatSocket.json.send({
      '@class': _ns + 'ChatObject',
      user: $("#username").val() || user,
      message: message,
      type: "MESSAGE",
      room: room
    });
    //chatSocket.emit('chatevent', {user: user, message: message});
  });

  $(".command-form").on('submit', function (e) {
    e.preventDefault();

    // get the message and clear the form
    var command = $('.command-form .cmd').val();
    $('.command-form .cmd').val('');

    apiSocket.json.send({
        '@class': _ns + 'CommandObject',
        command: command,
        room: room
    });
  });

  $(".room-form").on("submit", function (e) {
    e.preventDefault();

    sendCommand("New chatroom for me", "CREATE");
  });
})();
