import "jest";
import { BuildStatusItem } from "./build-status-item";

describe('BuildStatusItem', () => {
    describe("JSON Parsing", () => {
        var jsonToParse = {
            "statistics": {
                "Tests run": "6",
                "Failures": "2",
                "Errors": "3",
                "Skipped": "0"
            },
            "result": "BUILD FAILED",
            "time": "22.341 s",
            "endTime": "2021-02-12T18:32:20+01:00",
            "commitSHA": "271d2e72d3073703d888512314a776a1512f3b52"
        }
        var buildStatusItem = BuildStatusItem.fromJson(jsonToParse);

        it("Parses success correctly", () => {
            expect(buildStatusItem.success).toEqual(false);
        });
        it("Parses commitSHA correctly", () => {
            expect(buildStatusItem.commitSHA).toEqual("271d2e72d3073703d888512314a776a1512f3b52");
        });
        it("Parses endTime correctly", () => {
            expect(buildStatusItem.timeEnd.getHours()).toEqual(18);
            expect(buildStatusItem.timeEnd.getDate()).toEqual(12);
            expect(buildStatusItem.timeEnd.getSeconds()).toEqual(20);
        });
        it("Parses statistic correctly", () => {
            expect(buildStatusItem.numberOfTestsPassed).toEqual(1);
            expect(buildStatusItem.numberOfTestsFailed).toEqual(5);
        });
    })
})