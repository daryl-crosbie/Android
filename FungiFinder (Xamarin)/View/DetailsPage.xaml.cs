using FungiFinder.Model;
using FungiFinder.ViewModel;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace FungiFinder.View
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class DetailsPage : ContentPage
    {
        private readonly object DataContext;

        public DetailsPage()
        {
            InitializeComponent();
        }
        public DetailsPage(Mushrooms mushroom)
        {
            InitializeComponent();
            BindingContext = new FungiDetailsView(mushroom);
            DataContext = BindingContext;
            // alternatively as already instantiated in the xml
            //((FungiDetailsView)BindingContext).Mushroom = mushroom;
            
        }
    }
}