import { SystemUpdate } from "@material-ui/icons";
import { BuildStatusItem } from "./build-status-item";

export class BuildStatusProvider {
    static async getAllBuildsFromCIServer() : Promise<BuildStatusItem[]> {
        // Dummy promise
        var historyFetch = await fetch("/api/history");
        var historyAsJson = await historyFetch.json();
        var buildStatusItems: BuildStatusItem[] = historyAsJson.builds.map((buildStatusItemAsJson: any) => {
            return BuildStatusItem.fromJson(buildStatusItemAsJson);
        });
        var sortedBuildStatusItem = BuildStatusProvider.sortBuildsItemsByDateDescending(buildStatusItems);
        return sortedBuildStatusItem
    }

    private static sortBuildsItemsByDateDescending(items: BuildStatusItem[]) {
        return items.sort((itemA, itemB) => itemB.timeEnd.getTime() - itemA.timeEnd.getTime())
    }
}