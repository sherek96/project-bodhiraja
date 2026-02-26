import React from 'react';
import { Box, AppBar, Toolbar, Typography, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText } from '@mui/material';
import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';

// The width of our side menu
const drawerWidth = 240;

function Layout({ children }) {
  return (
    <Box sx={{ display: 'flex' }}>
      
      {/* 1. THE TOP NAVBAR */}
      <AppBar 
        position="fixed" 
        sx={{ 
          zIndex: (theme) => theme.zIndex.drawer + 1,
          backgroundColor: '#1976d2' // A nice professional blue
        }}
      >
        <Toolbar>
          <Typography variant="h6" noWrap component="div">
            Bodhiraja OMS
          </Typography>
        </Toolbar>
      </AppBar>

      {/* 2. THE SIDEBAR (Drawer) */}
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box' },
        }}
      >
        <Toolbar /> {/* This creates empty space so the menu starts BELOW the Navbar */}
        <Box sx={{ overflow: 'auto' }}>
          <List>
            
            {/* Dashboard Link */}
            <ListItem disablePadding>
              <ListItemButton>
                <ListItemIcon>
                  <DashboardIcon />
                </ListItemIcon>
                <ListItemText primary="Dashboard" />
              </ListItemButton>
            </ListItem>

            {/* Students Link */}
            <ListItem disablePadding>
              <ListItemButton>
                <ListItemIcon>
                  <PeopleIcon />
                </ListItemIcon>
                <ListItemText primary="Students" />
              </ListItemButton>
            </ListItem>

          </List>
        </Box>
      </Drawer>

      {/* 3. THE MAIN CONTENT AREA */}
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Toolbar /> {/* Empty space so content isn't hidden behind the Navbar */}
        
        {/* 'children' is a special React prop. 
            Whatever page we are on will be injected right here! */}
        {children} 
        
      </Box>
    </Box>
  );
}

export default Layout;