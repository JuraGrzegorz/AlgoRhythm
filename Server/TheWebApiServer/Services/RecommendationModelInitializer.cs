using Microsoft.AspNetCore.Mvc.Filters;
using TheWebApiServer.Data;

namespace TheWebApiServer.Services
{
    public class RecommendationModelInitializer : IHostedService, IDisposable
    {
        private readonly IServiceProvider _serviceProvider;
        private Timer _timer;
        public RecommendationModelInitializer(IServiceProvider serviceProvider)
        {
            _serviceProvider = serviceProvider;
        }

        public void Dispose()
        {
            _timer?.Dispose();
        }

        public async Task StartAsync(CancellationToken cancellationToken)
        {
            _timer = new Timer(Train, null, TimeSpan.Zero, TimeSpan.FromHours(24));
        }

        private void Train(object state)
        {
            using (var scope = _serviceProvider.CreateScope())
            {
                var context = scope.ServiceProvider.GetRequiredService<DataContext>();
                var recommendationModel = scope.ServiceProvider.GetRequiredService<RecommendationModel>();
                recommendationModel.TrainModel(context);
            }
        }

        public Task StopAsync(CancellationToken cancellationToken)
        {
            return Task.CompletedTask;
        }
    }
}
