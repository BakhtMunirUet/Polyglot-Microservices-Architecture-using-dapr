using Dapr.Client;
using Google.Protobuf.WellKnownTypes;
using Microsoft.AspNetCore.DataProtection.KeyManagement;
using Microsoft.AspNetCore.Mvc;

namespace customer_service.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CustomerController : ControllerBase
    {

        private readonly DaprClient _daprClient;


        public CustomerController(DaprClient daprClient)
        {
            _daprClient = daprClient;
        }

        [HttpGet(Name = "getCustomerInfo/{customerId}")]
        public async Task<IActionResult> getCustomerInfo(string customerId)
        {
            var customer = await _daprClient.GetStateAsync<Customer>("statestore", customerId);
    
            return Ok(customer);
        }


        [HttpPost(Name = "createCustomer")]
        public async Task<IActionResult> createCustomer([FromBody] Customer customer)
        {
            customer.CustomerId = Guid.NewGuid().ToString();

            await _daprClient.SaveStateAsync("statestore", customer.CustomerId, customer);
            var res = await _daprClient.GetStateAsync<Customer>("statestore", customer.CustomerId);
            return Ok(res);
        }
    }
}
