import React, { useEffect, useState } from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';
import { BuildStatus } from './model/build-status-item';
import { BuildStatusProvider } from './model/build-status-provider';
import { BuildStatusTimeline } from './components/build-status-timeline';
import { AppBar, IconButton, Toolbar } from '@material-ui/core';
import MainPageAppBar from './components/app-bar';

export default function App() {
  const [buildsLoading, setBuildsLoading] = useState(true);
  const [builds, setBuilds] = useState<BuildStatus[]>([]);

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
      <Container maxWidth="md">
        {
          buildsLoading ? <h3>Loading</h3> : <BuildStatusTimeline builds={builds}/>
        }
      </Container>
    </React.Fragment>
  );
}
