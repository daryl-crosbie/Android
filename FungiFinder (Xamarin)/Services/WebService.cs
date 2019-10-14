using FungiFinder.Model;
using FungiFinder.Services;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using Xamarin.Forms;

[assembly: Dependency(typeof(WebService))]
namespace FungiFinder.Services
{
    public class WebService : IDataService 
    {
        HttpClient httpClient;
        HttpClient Client => httpClient ?? (httpClient = new HttpClient());

        public async Task<IEnumerable<Mushrooms>> GetMushroomsAsync()
        {
            return Mushrooms.FromJson(await Client.GetStringAsync("https://raw.githubusercontent.com/daryl-crosbie/JSON/master/Mushrooms.json"));

            /*var json = await Client.GetStringAsync("https://raw.githubusercontent.com/daryl-crosbie/JSON/master/Mushrooms.json");
            var shrooms = Mushrooms.FromJson(json);
            return shrooms;
            */
        }

    }
}
