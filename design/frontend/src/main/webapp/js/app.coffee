game = {}
playerID = null
game.myTurn = false
game.started = false
game.numOfRetries = 0
game.initialized = false
_.templateSettings =
  evaluate: /\{\[([\s\S]+?)\]\}/g
  interpolate: /\{\{([\s\S]+?)\}\}/g

$ ->
  paintDeckAndRestartNextTurnButtons = ->
    $("#deck").append "<div class=\"card\"><div class=\"face front\"></div><div class=\"face back\"></div></div>"
    $("#deck").click drawCard
    $("#deck").toggleClass "hidden"
    $("#controlButtons").append "<button id=\"nextTurn\" class=\"uskociButton btn btn-primary btn-large\">Next player!</button>"
    $("#controlButtons").append "<button id=\"restartGame\" class=\"uskociButton btn btn-primary btn-large\">Restart game</button>"
    $("#nextTurn").click nextTurn
    $("#restartGame").click restartGame
  checkLastMessageAndPerformAction = (response) ->
    unless response.actionStatus is "OK"
      $("#turnInfo").html(response.actionStatus).addClass "alert-error"
      return
    $("#turnInfo").html(response.actionStatus).removeClass "alert-error"
    
    #noinspection FallthroughInSwitchStatementJS
    switch response.lastAction["action"]
      when "startGame"
        drawJoinPlayerControls response  unless game.started
      when "restartGame"
        game.started = false
        subSocket.push jQuery.stringifyJSON(
          userId: ""
          action: "startGame"
          cardId: ""
          gameId: "0"
        )
        location.reload()
    repaintTable response  if game.initialized
  drawJoinPlayerControls = (response) ->
    return  if game.started
    uskociCardsCollection.fetch()
    $("#startGame").remove()
    $("#playerSelect").toggleClass "hidden"
    $("#gameNavigation").append "<button id=\"joinGame\" class=\"uskociButton btn btn-primary btn-large\">Join game</button>"
    $("#joinGame").click joinGame_MouseClick
    $("#game").toggleClass "hidden"
    content.append "Game started! "
    content.append "Player " + response.currentPlayerId + " is on the move."
    game.started = true
  assignValues = (response) ->
    game.playerCards = response.playersCards[playerID]
    game.playersPoints = response.playersPoints
    game.numberOfPlayers = response.numberOfPlayersJoined
    game.myTurn = response.currentPlayerId is playerID
    game.currentPlayerID = response.currentPlayerId
    game.resourcePiles = response.playersResources
    game.playersResourcesByType = response.playersResourcesByType
    game.actionStatus = response.actionStatus
  repaintTable = (response) ->
    checkPlayerOnTheMove()
    repaintResourcePiles()
    repaintHand()
    modifyGameStatus response  if response isnt null
  modifyGameStatus = (gameStatusResponse) ->
    content.append "<p>PlayerID / lastAction / cardID: " + gameStatusResponse.lastAction["userId"] + " / " + gameStatusResponse.lastAction["action"] + " / " + gameStatusResponse.lastAction["cardId"] + "</p>"
  checkIfGameStarted = ->
    subSocket.push jQuery.stringifyJSON(
      userId: ""
      action: "isGameStarted"
      cardId: ""
      gameId: "0"
    )
  joinGame_MouseClick = ->
    return  if game.playerCards is null
    $("#game").height 800
    repaintTable null
    paintDeckAndRestartNextTurnButtons()
    $("#welcomeScreen").remove()
    game.initialized = true
  startGame_MouseClick = ->
    subSocket.push jQuery.stringifyJSON(
      userId: playerID
      action: "startGame"
      cardId: ""
      gameId: "0"
    )
  nextTurn = ->
    subSocket.push jQuery.stringifyJSON(
      userId: playerID
      action: "setNextTurn"
      cardId: ""
      gameId: "0"
    )
  drawCard = ->
    subSocket.push jQuery.stringifyJSON(
      userId: playerID
      action: "drawCard"
      cardId: ""
      gameId: "0"
    )
  repaintResourcePiles = ->
    uskociResourcesView = new App.Views.UskociResources(uskociCardsCollection, game.playersResourcesByType)
  setPlayer = ->
    return  unless game.started
    playerID = $("select option:selected").attr("value")
    subSocket.push jQuery.stringifyJSON(
      userId: playerID
      action: "playersCards"
      cardId: ""
      gameId: "0"
    )
  
  #Navigation
  repaintHand = ->
    uskociHandView = new App.Views.UskociHand(uskociCardsCollection, game.playerCards)
  checkPlayerOnTheMove = ->
    if game.myTurn
      $("#turnInfo").html "Your turn!"
    else
      $("#turnInfo").html "Wait... Player " + game.currentPlayerID + " is on turn."
  restartGame = ->
    $ ->
      $("#dialog-confirm").dialog
        resizable: false
        height: 140
        modal: true
        buttons:
          "Restart game": ->
            subSocket.push jQuery.stringifyJSON(
              userId: ""
              action: "restartGame"
              cardId: ""
              gameId: "0"
            )

          Cancel: ->
            $(this).dialog "close"


  App =
    Models: {}
    Collections: {}
    Views: {}

  template = (id) ->
    _.template $("#" + id).html()

  App.Models.UskociCard = Backbone.Model.extend({})
  App.Views.UskociCard = Backbone.View.extend(
    template: template("cardTemplate")
    events:
      click: "cardClicked"
      mouseenter: "hoverIn"
      mouseleave: "hoverOut"

    hoverIn: (e) ->
      e.preventDefault()
      descriptionDIV = "<div class=\"description\">" + @model.get("description") + "</div>"
      @$el.children(":first").append descriptionDIV
      @$el.children(":first").toggleClass "zoomed"

    hoverOut: (e) ->
      e.preventDefault()
      @$el.children(":first").toggleClass "zoomed"
      @$el.find(".description").remove()

    cardClicked: ->
      subSocket.push jQuery.stringifyJSON(
        userId: playerID
        action: "playCard"
        cardId: @model.get("id")
        gameId: "0"
      )

    render: (index) ->
      uskociCardElement = $(@template(@model.toJSON()))
      uskociCardElement.css
        left: 182.5 * (index % 4)
        top: 249 * Math.floor(index / 4)

      @$el.html uskociCardElement
      this
  )
  App.Views.UskociSmallCard = Backbone.View.extend(
    template: template("smallCardTemplate")
    render: ->
      @$el.html @template(@model.toJSON())
      this
  )
  App.Collections.UskociCards = Backbone.Collection.extend(
    model: App.Models.UskociCard
    url: document.location + "rest/cards"
  )
  App.Views.UskociHand = Backbone.View.extend(
    el: $("#playerCards")
    initialize: (uskociCardsCollection, cardsInHand) ->
      @collection = _(uskociCardsCollection.filter((uskociCard) ->
        _.include cardsInHand, uskociCard.id
      ))
      @render()

    render: ->
      @$el.empty()
      @collection.each @addOne, this
      this

    addOne: (uskociCard) ->
      uskociCardView = new App.Views.UskociCard(model: uskociCard)
      index = @collection.indexOf(uskociCard)
      @$el.append uskociCardView.render(index).el
  )
  App.Views.UskociResources = Backbone.View.extend(
    initialize: (uskociCardsCollection, playersResourcesByType) ->
      @collection = uskociCardsCollection
      @playersResourcesByType = playersResourcesByType
      @render()

    render: ->
      try
        playersResourcesByType = @rotateArray(@playersResourcesByType, parseInt(playerID, 10) - 1)
        numberOfPlayers = _.keys(playersResourcesByType).length
        _.each playersResourcesByType, ((playerResourcesByType) ->
          _.each _.keys(playerResourcesByType), ((resourceType) ->
            selector = "#resource" + (_.indexOf(playersResourcesByType, playerResourcesByType) + 1) + " ." + resourceType
            $(selector).empty()
            _.each playerResourcesByType[resourceType], ((id) ->
              uskociSmallCard = new App.Models.UskociCard(
                id: id
                position: @getSmallCardPosition(id)
              )
              uskociSmallCardView = new App.Views.UskociSmallCard(model: uskociSmallCard)
              uskociSmallCardViewElement = $(uskociSmallCardView.render().el)
              uskociSmallCardViewElement.children(":first").css top: 20 * _.indexOf(playerResourcesByType[resourceType], id) + 20
              $(selector).append uskociSmallCardViewElement
            ), this
          ), this
        ), this
      catch e
        console.log e.message

    rotateArray: (array, offset) ->
      rotatedArray = []
      i = 0

      while i < _.size(array)
        rotatedArray.push array[((i + offset) % _.size(array)) + 1]
        i++
      rotatedArray

    getSmallCardPosition: (id) ->
      regex = /(.+)px\s(.+)px/
      originalPosition = @collection.get(id).get("position")
      regexMatch = originalPosition.match(regex)
      positionX_scaled = (parseFloat(regexMatch[1]) / 2).toString()
      positionY_scaled = (parseFloat(regexMatch[2]) / 2).toString()
      positionX_scaled + "px " + positionY_scaled + "px"
  )
  uskociCardsCollection = new App.Collections.UskociCards()
  content = $("#console")
  socket = $.atmosphere
  request =
    url: document.location.toString() + "rest" + "/gamecontrol"
    contentType: "application/json"
    logLevel: "debug"
    transport: "long-polling"
    fallbackTransport: "long-polling"

  request.onOpen = (response) ->
    content.html $("<p>",
      text: "Uskoci (gameControl) connected using " + response.transport
    )

  request.onMessage = (response) ->
    message = response.responseBody
    try
      json = jQuery.parseJSON(message)
      assignValues json  if playerID isnt null and json.actionStatus is "OK"
      checkLastMessageAndPerformAction json
    catch e
      console.log "This doesn't look like a valid JSON: ", message.data
      return

  request.onError = (response) ->
    content.html $("<p>",
      text: "Sorry, but there's some problem with your socket or the server is down"
    )

  subSocket = socket.subscribe(request)
  checkIfGameStarted()
  $("#startGame").click startGame_MouseClick
  $("#playerSelect").change setPlayer