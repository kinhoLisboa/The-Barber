package com.theBarber.TheBarber.WSServices;

public record WhatsAppMessageRequest(
        String phone,
        String message
    ) {}
