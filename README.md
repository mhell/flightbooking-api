![Lexicon Logo](https://lexicongruppen.se/media/wi5hphtd/lexicon-logo.svg)
# ✈️ Flight Reservation – Project Test

This project is a Spring Boot application connected to a backend database to manage flight and booking data.

The system allows users to:

- View a list of all flights
- See only available flights
- Book a flight using their name and email
- Cancel a booking using their email
- Check all their bookings by email

## Objective:
Your task is to enhance the existing Flight Reservation System by integrating an AI assistant that can handle user queries related to offer available flight bookings, cancellations, and booking a flight.

---

## Getting Started

1. Fork this repository
2. Clone your fork
3. Run the application
4. Visit the API documentation at `http://localhost:8080/swagger-ui.html`
5. Explore the app's features
6. Integrate your AI assistant API
7. OPTIONAL: Implement a user-friendly chatbot interface using ReactJS

---

## Requirements

Integrate the app with **OpenAI** using your API key.

Create a new API endpoint that accepts **user input as a question or request**.  
The assistant should understand the message and help the user with:

- Search fights
- Book a flight
- Cancel a flight

### Build a helpful assistant:

- Define a clear and well-crafted **system message** to guide the assistant's behavior
- Use **in-memory chat history** to maintain the context of the conversation with a limited number of messages.
- Implement **tool calling** so the assistant can trigger actions like searching flights or booking a seat based on the
  user's request

Make sure your assistant can guide the user step-by-step and handle missing or unclear information gracefully.

### Optional: Frontend Implementation:

After integrating the Backend API with the AI model, create a user-friendly chatbot interface using ReactJS that:

- Implements an interactive chatbot component that allows users to:
    - Ask questions about flights
    - Get assistance with bookings
    - Receive real-time responses from the AI assistant



