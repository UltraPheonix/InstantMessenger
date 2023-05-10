module instantmessengerzaga.instantmessenger {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens instantmessengerzaga.instantmessenger to javafx.fxml;
    exports instantmessengerzaga.instantmessenger;
}