package apple.configs.registered;

import apple.utilities.threading.service.queue.TaskHandlerQueue;

public class AppleConfigService {
    private static final TaskHandlerQueue instance = new TaskHandlerQueue(20, 0, 0);

    public static TaskHandlerQueue get() {
        return instance;
    }
}
