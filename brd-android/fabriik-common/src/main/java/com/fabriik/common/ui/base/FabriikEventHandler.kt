package com.fabriik.common.ui.base

interface FabriikEventHandler<Event> {
    fun handleEvent(event: Event)
}