// hello.test.js

import React from "react";
import { render, unmountComponentAtNode } from "react-dom";
import { act } from "react-dom/test-utils";
import { BuildStatusItem } from "../model/build-status-item";
import { BuildStatusCard } from "./build-status-card";

let container: HTMLDivElement | null = null;
beforeEach(() => {
  // setup a DOM element as a render target
  container = document.createElement("div");
  document.body.appendChild(container);
});

afterEach(() => {
  // cleanup on exiting
  if (container != null) {
      unmountComponentAtNode(container);
      container.remove();
      container = null;
  }
});

describe("Test that build status card render data correctly", () => {
    var buildStatusItemSuccess = new BuildStatusItem({
        commitSHA: "ABC123",
        success: true,
        duration: "100 s",
        testFailures: 0,
        tests: 4,
        timeEnd: new Date(),
    })
    var buildStatusItemFailed = new BuildStatusItem({
        commitSHA: "ABC123",
        success: false,
        duration: "100 s",
        testFailures: 2,
        tests: 4,
        timeEnd: new Date(),
    })
    it("Render commit sha", () => {
        act(() => {
            render(<BuildStatusCard buildStatus={buildStatusItemSuccess} />, container);
        });
        expect(container!.textContent).toContain("ABC123");
    })
    it("Render success", () => {
        act(() => {
            render(<BuildStatusCard buildStatus={buildStatusItemSuccess} />, container);
        });
        expect(container!.textContent).toContain("Build Succeeded");
        act(() => {
            render(<BuildStatusCard buildStatus={buildStatusItemFailed} />, container);
        });
        expect(container!.textContent).toContain("Build Failed");
    })
    it("Render duration", () => {
        act(() => {
            render(<BuildStatusCard buildStatus={buildStatusItemSuccess} />, container);
        });
        expect(container!.textContent).toContain("Took 100 s");
    })
});