package se.lexicon.flightbooking_api.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    public Flux<String> processChatQuery(String chatId, String chatQuery) {
        var prompt = chatClient.prompt()
                .user(chatQuery)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId));

        return prompt.stream().content();
    }
}
