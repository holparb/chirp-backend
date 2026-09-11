package com.holparb.chirpbackend.domain.exception

class InvalidChatSizeException: RuntimeException("There must be at least 2 unique participants")