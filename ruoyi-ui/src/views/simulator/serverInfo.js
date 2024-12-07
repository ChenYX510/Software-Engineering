const baseIP = 'localhost';
const serverInfo =
{
    baseIP: baseIP,
    baseURL: `http://${baseIP}:8000`,
    baseURL_infection: `http://${baseIP}:8080`,
    baseURL_measure: `http://${baseIP}:8000`,
    baseURL_three: `http://${baseIP}:8008`,
    baseURL_file: `http://${baseIP}:8006`,
    baseURL_measure_python: `http://${baseIP}:8006`,
    // baseURL_resilience: 'http://139.196.227.120:5000',
    baseURL_resilience: `http://${baseIP}:8007`,
    baseURL_reverse:`http://${baseIP}:9090`,
    baseURL_explode: `http://${baseIP}/tj-explode`,
    baseURL_LLM_HELPER: `http://${baseIP}:8012`,
    baseURL_LLM: `http://${baseIP}:8011`,
};

// const serverInfo =
// {
//     baseIP: baseIP,
//     baseURL: process.env['VUE_APP_BASE_API_TJ'],
//     baseURL_infection: process.env['VUE_APP_BASE_API_TJ_INFECTION'],
//     baseURL_measure: process.env['VUE_APP_BASE_API_TJ'],
//     baseURL_three: process.env['VUE_APP_BASE_API_TJ_HTTPD'],
//     baseURL_measure_python: process.env['VUE_APP_BASE_API_TJ_FILE'],
//     baseURL_file: process.env['VUE_APP_BASE_API_TJ_FILE'],
//     baseURL_resilience: process.env['VUE_APP_BASE_API_TJ_URBAN'],
//     baseURL_reverse: process.env['VUE_APP_BASE_API_TJ_REVERSE'],
//     baseURL_explode: process.env['VUE_APP_BASE_API_TJ_EXPLODE'],
//     baseURL_LLM_HELPER: process.env['VUE_APP_BASE_API_TJ_LLM_HELPER'],
//     baseURL_LLM: process.env['VUE_APP_BASE_API_TJ_LLM'],
// };


export default serverInfo;
