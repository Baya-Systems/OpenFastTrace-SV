// This is a sample SystemVerilog file used for testing the SVRequirementParser.
// It contains example comments and code to validate the parser's functionality.

module sample_module;
    // [req~id~1] Title: This is a requirement description
    // [covers test1, test2]
    // [depends req~id~2]
    // [needs feature1, feature2]
    // [tags tag1, tag2]

    initial begin
        // Sample code for testing
        $display("Hello, SystemVerilog!");
    end
endmodule