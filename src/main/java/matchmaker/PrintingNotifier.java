package matchmaker;

import java.util.stream.Collectors;

public class PrintingNotifier implements Notifier {

    @Override
    public void sendNotification(Match match) {
        match.getMatched().forEach(r -> {
            System.out.println(String.format("Dear %d, you have been matched with %s", r.getId(),
                    match.getMatched().stream().filter(o -> o.getId() != r.getId()).collect(Collectors.toSet())));
        });
    }

}
