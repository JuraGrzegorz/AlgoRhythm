using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace TheWebApiServer.Migrations
{
    /// <inheritdoc />
    public partial class addingThumbnailDataToSongTable : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<byte[]>(
                name: "ThumbnailData",
                table: "Songs",
                type: "varbinary(max)",
                nullable: false,
                defaultValue: new byte[0]);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "ThumbnailData",
                table: "Songs");
        }
    }
}
