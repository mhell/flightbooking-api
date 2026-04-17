package se.lexicon.flightbooking_api.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import se.lexicon.flightbooking_api.dto.ChatRequestDTO;

@Service
public class FlightBookingAssistantImpl implements FlightBookingAssistant {
    private final ChatClient chatClient;

    public FlightBookingAssistantImpl(ChatClient.Builder builder, ChatMemory chatMemory, FlightBookingService flightBookingService) {
        this.chatClient = builder.
                defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor())
                .defaultTools(flightBookingService)
                .build();
    }

    public Flux<String> processChatQuery(ChatRequestDTO chatRequest) {
        var prompt = chatClient.prompt()
                .user(chatRequest.message())
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatRequest.chatId()));

        return prompt.stream().content();
    }
}
