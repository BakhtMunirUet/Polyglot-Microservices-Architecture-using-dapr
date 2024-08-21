const express = require('express');
const bodyParser = require('body-parser');
const { DaprClient, HttpMethod } = require('dapr-client');
const { v4: uuidv4 } = require('uuid');

const app = express();
app.use(bodyParser.json());



const daprPort = process.env.DAPR_HTTP_PORT || 3500; // Dapr sidecar HTTP port
const daprHost = `http://localhost:${daprPort}`;
const daprClient = new DaprClient(daprHost, daprPort);

app.post('/createItems', async (req, res) => {
    const item = req.body;
    console.log('Received items:', item);

    const userItem = {
        itemId: uuidv4(),
        name: item.name,
        descriptions: item.descriptions
    };
    console.log('Received items:', userItem);
    // Save state using Dapr
    await daprClient.state.save('statestore', [
        {
            key: userItem.itemId,
            value: userItem,
        },
    ]);

    res.status(200).send(await daprClient.state.get('statestore', userItem.itemId));
});

app.get('/item', async (req, res) => {
    // Get state using Dapr
    const itemId = req.query.itemId
    const item = await daprClient.state.get('statestore', itemId);
    res.send(item);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Node.js API listening on port ${PORT}`);
});