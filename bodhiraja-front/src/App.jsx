import React from 'react';
import Layout from './components/Layout';

function App() {
  return (
    <Layout>
      {/* This h1 tag becomes the 'children' inside the Layout */}
      <h1>Welcome to the Bodhiraja Dashboard</h1>
      <p>Select an option from the menu on the left.</p>
    </Layout>
  );
}

export default App;