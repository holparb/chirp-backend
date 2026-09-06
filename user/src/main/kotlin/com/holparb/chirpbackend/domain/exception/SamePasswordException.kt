package com.holparb.chirpbackend.domain.exception

class SamePasswordException: RuntimeException("Old password is the same as the new password")