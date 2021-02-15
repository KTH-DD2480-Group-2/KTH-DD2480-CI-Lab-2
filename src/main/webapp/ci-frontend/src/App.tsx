import React, { useEffect, useState } from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';
import { BuildStatusItem } from './model/build-status-item';
import { BuildStatusProvider } from './model/build-status-provider';
import { BuildStatusTimeline } from './components/build-status-timeline';
import { AppBar, IconButton, Toolbar } from '@material-ui/core';
import MainPageAppBar from './components/app-bar';
import {
  Route,
  BrowserRouter as Router,
  Switch,
} from "react-router-dom";

export default function App() {
  const [buildsLoading, setBuildsLoading] = useState(true);
  const [builds, setBuilds] = useState<BuildStatusItem[]>([]);

  useEffect(() => {
    BuildStatusProvider.getAllBuildsFromCIServer().then((fetchedBuilds) => {
      setBuilds(fetchedBuilds);
      console.log(fetchedBuilds);
      setBuildsLoading(false);
    });
  }, [])

  return (
    <React.Fragment>
      <CssBaseline />
      <MainPageAppBar/>
      <Router basename="dashboard">
        <Switch>
            <Route path="/">
              <Container maxWidth="md">
                {
                  buildsLoading ? <h3>Loading</h3> : <BuildStatusTimeline builds={builds}/>
                }
              </Container>
            </Route>
        </Switch>
      </Router>
    </React.Fragment>
  );
}
