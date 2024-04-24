using System.ComponentModel.DataAnnotations.Schema;

namespace TheWebApiServer.Models
{
    public class Genre
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
    }
}
