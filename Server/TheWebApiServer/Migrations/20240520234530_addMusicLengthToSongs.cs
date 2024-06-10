using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace TheWebApiServer.Migrations
{
    /// <inheritdoc />
    public partial class addMusicLengthToSongs : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<float>(
                name: "MusicLength",
                table: "Songs",
                type: "real",
                nullable: false,
                defaultValue: 0f);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "MusicLength",
                table: "Songs");
        }
    }
}
