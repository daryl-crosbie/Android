using System;
using System.Globalization;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FungiFinder.Model
{
    public partial class Mushrooms
    {
        [JsonProperty("Name")]
        public string Name { get; set; }

        [JsonProperty("Location")]
        public string Location { get; set; }

        [JsonProperty("Details")]
        public string Details { get; set; }

        [JsonProperty("Image")]
        public Uri Image { get; set; }

        [JsonProperty("Usage")]
        public string Usage { get; set; }

        [JsonProperty("SeasonStart")]
        public long SeasonStart { get; set; }

        [JsonProperty("SeasonEnd")]
        public long SeasonEnd { get; set; }

        [JsonProperty("Latitude")]
        public double Latitude { get; set; }

        [JsonProperty("Longitude")]
        public double Longitude { get; set; }
    }

    public partial class Mushrooms
    {
        public static Mushrooms[] FromJson(string json) => JsonConvert.DeserializeObject<Mushrooms[]>(json, FungiFinder.Model.Converter.Settings);
    }

    public static class Serialize
    {
        public static string ToJson(this Mushrooms[] self) => JsonConvert.SerializeObject(self, FungiFinder.Model.Converter.Settings);
    }

    internal static class Converter
    {
        public static readonly JsonSerializerSettings Settings = new JsonSerializerSettings
        {
            MetadataPropertyHandling = MetadataPropertyHandling.Ignore,
            DateParseHandling = DateParseHandling.None,
            Converters =
            {
                new IsoDateTimeConverter { DateTimeStyles = DateTimeStyles.AssumeUniversal }
            },
        };
    }
}
