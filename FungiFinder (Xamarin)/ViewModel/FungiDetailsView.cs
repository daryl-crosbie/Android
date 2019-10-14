using FungiFinder.Model;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Essentials;

namespace FungiFinder.ViewModel
{
    public class FungiDetailsView : BaseViewModel
    {
        public Command ShowMap { get; }

        Mushrooms mushroom;
        public Mushrooms Mushroom
        {
            get => mushroom;
            set
            {
                if (mushroom == value)
                    return;
                mushroom = value;
                OnPropertyChanged();
            }
        }
        public FungiDetailsView()
        {
            ShowMap = new Command(async () => await ShowMapAsync());
        }

        public FungiDetailsView(Mushrooms mushroom) : this()
        {
            Mushroom = mushroom;
            Title = $"{Mushroom.Name} Details";
        }

        async Task ShowMapAsync()
        {
            try
            {
                await Map.OpenAsync(mushroom.Latitude, mushroom.Longitude);
            }catch
            {
                await Application.Current.MainPage.DisplayAlert("Error", "Cannot open map", "OK");
            }
           
        }
    }
}
