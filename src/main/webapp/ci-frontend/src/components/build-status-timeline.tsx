import React, { createRef, FunctionComponent, useEffect, useRef } from "react"
import { BuildStatusItem } from "../model/build-status-item"
import Timeline from '@material-ui/lab/Timeline';
import TimelineItem from '@material-ui/lab/TimelineItem';
import TimelineSeparator from '@material-ui/lab/TimelineSeparator';
import TimelineConnector from '@material-ui/lab/TimelineConnector';
import TimelineContent from '@material-ui/lab/TimelineContent';
import TimelineDot from '@material-ui/lab/TimelineDot';
import { TimelineOppositeContent } from '@material-ui/lab';
import { BuildStatusCard } from "./build-status-card";
import { useParams } from "react-router";

type BuildStatusTimelineProps = {
    builds: BuildStatusItem[],
}
  
export const BuildStatusTimeline: FunctionComponent<BuildStatusTimelineProps> = ({ builds }) =>{ 
    const { commitSHA } = useParams<{commitSHA: string}>();
    const scrollTarget = useRef<HTMLInputElement>(null);

    useEffect(() => {
        if (scrollTarget) {
            scrollTarget.current?.scrollIntoView({
                behavior: "smooth",
                block: "center",
            });
        }
    }, [commitSHA]);
    

    return (
        <div>
            <Timeline align="right">
                {
                    builds.map(build => {
                        return <TimelineItem key={build.commitSHA}>
                            <TimelineOppositeContent>
                                <div ref={build.commitSHA == commitSHA ? scrollTarget : null}>
                                    <BuildStatusCard isURLTarget={build.commitSHA == commitSHA} buildStatus={build}/>
                                </div>
                            </TimelineOppositeContent>
                            <TimelineSeparator>
                                <TimelineConnector />
                                <TimelineDot variant="outlined" style={{
                                    borderColor: build.success ? "#00aa00" : "#ff0000"
                                }} />
                                <TimelineConnector />
                            </TimelineSeparator>
                            <TimelineContent style={{flex: "none", alignItems: "center", display: "flex"}}>
                                <div>
                                    <i>{build.timeEndDMY}</i>
                                    <br/>
                                    <b>{build.timeEndHMS}</b>
                                </div>
                            </TimelineContent>
                        </TimelineItem>
                    })
                }
            </Timeline>
        </div>)
}