using FungiFinder.Model;
using System;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Xamarin.Essentials;
using Xamarin.Forms;

namespace FungiFinder.ViewModel
{
    class FungiViewModel : BaseViewModel
    {
        public Collection<Mushrooms> Shrooms { get; }
        public ObservableCollection<Mushrooms> ListingShrooms { get; }

        public Command ShowEdibles { get; }
        public Command ShowMagic { get; }
        public Command ShowToxic { get; }
        public Command GetAll { get; }
        public Command GetInSeason { get; }
        public Command GetNearestCommand { get; }
        int today;
        public int Today
        {
            get { return today; }
            set { today = value; }
        }
        public FungiViewModel()
        {
            Title = "Fungi Finder";
            Shrooms = new Collection<Mushrooms>();
            ListingShrooms = new ObservableCollection<Mushrooms>();
            Task.Run(() => GetShroomsAsync()).Wait();
            ShowEdibles = new Command(async () => await ShowTypeAsync("Edible"));
            ShowMagic = new Command(async () => await ShowTypeAsync("Magic"));
            ShowToxic = new Command(async () => await ShowTypeAsync("Toxic"));
            GetAll = new Command(async () => await GetAllAsync());
            GetInSeason = new Command(async () => await GetInSeasonAsync());
            GetNearestCommand = new Command(async () => await GetClosestAsync());
            
        }

        async Task ShowTypeAsync(string type)
        {
            if (IsBusy)
                return;
            try
            {
                IsBusy = true;
                ListingShrooms.Clear();
                foreach(var m in Shrooms)
                {
                    if(m.Usage.Equals(type))
                        ListingShrooms.Add(m);
                }
            }catch(Exception e)
            {
                Debug.WriteLine($"Error retieving shrooms: {e.Message}");
                await Application.Current.MainPage.DisplayAlert("Error", e.Message, "Ok");
            }
            finally
            {
                IsBusy = false;
            }
        }

        async Task GetShroomsAsync()
        {
            if (IsBusy)
                return;
            try
            {
                IsBusy = true;
                var mushies = await DataService.GetMushroomsAsync();
                Shrooms.Clear();
                ListingShrooms.Clear();
                foreach (var mushroom in mushies)
                {
                    Shrooms.Add(mushroom);
                    ListingShrooms.Add(mushroom);
                }
            }
            catch(Exception e)
            {
                Debug.WriteLine($"Error retieving shrooms: {e.Message}");
                await Application.Current.MainPage.DisplayAlert("Error", e.Message, "Ok");
            }
            finally
            {
                IsBusy = false;
            }
        }
        
        async Task GetClosestAsync()
        {
            if (IsBusy || ListingShrooms.Count == 0)
                return;
            try
            {
                var location = await Geolocation.GetLastKnownLocationAsync();
                if (location == null)
                {
                    location = await Geolocation.GetLocationAsync(new GeolocationRequest
                    {
                        DesiredAccuracy = GeolocationAccuracy.Low,
                        Timeout = TimeSpan.FromSeconds(30)
                    });
                }
                var first = ListingShrooms.OrderBy(m => location.CalculateDistance(
                    new Location(m.Latitude, m.Longitude), DistanceUnits.Miles)).FirstOrDefault();

                await Application.Current.MainPage.DisplayAlert("", first.Name, "Ok");
                
                
            }
            catch (Exception e)
            {
                Debug.WriteLine($"Unable to query location: {e.Message}");
                await Application.Current.MainPage.DisplayAlert("Error!", e.Message, "OK");
            }
        }

        async Task GetInSeasonAsync()
        {
            if (IsBusy)
                return;
            try
            {
                IsBusy = true;
                getNowAsInt();
                
                for(var i = 0; i < ListingShrooms.Count; i++)
                {
                    Mushrooms m = ListingShrooms.ElementAt(i);
                    if(!(Today >= m.SeasonStart && Today <= m.SeasonEnd))
                    {
                        ListingShrooms.Remove(m);
                        i--;
                    }
                }
                if(ListingShrooms.Count == 0)
                {
                    await Application.Current.MainPage.DisplayAlert("Sorry", "None known to be is season" , "Ok");
                }
            }
            catch(Exception e)
            {
                Debug.WriteLine($"Error retieving data: {e.Message}");
                await Application.Current.MainPage.DisplayAlert("Error", e.Message, "Ok");
            }
            finally
            {
                IsBusy = false;
            }
        }

        public void getNowAsInt()
        {
            string now = DateTime.Today.ToString("d");
            int i;
            if(!int.TryParse(now.Substring(now.IndexOf('/') + 1, now.Length-8), out i))
            {
                Application.Current.MainPage.DisplayAlert("Error", "Current date issue", "Ok");
            }
            Today = i;
        }

        async Task GetAllAsync()
        {
            ListingShrooms.Clear();
            try
            {
                foreach (var m in Shrooms)
                    ListingShrooms.Add(m);
            }catch(Exception e)
            {
                Debug.WriteLine($"Error retieving shrooms: {e.Message}");
                await Application.Current.MainPage.DisplayAlert("Error", e.Message, "Ok");
            }
            
        }
    }
}
