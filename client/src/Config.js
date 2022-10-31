class Config {
    // This is the config file for the project.
    // It uses environment variables which can be set in .env file or in the system.
    static SERVER_URL = process.env.REACT_APP_SERVER_URL;
    static CLIENT_URL = process.env.REACT_APP_CLIENT_URL;
}

export default Config;
