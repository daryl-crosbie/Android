using FungiFinder.Model;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace FungiFinder.Services
{
    public interface IDataService
    {
        Task<IEnumerable<Mushrooms>> GetMushroomsAsync();
    }
}
