/** @jsx React.createElement */
/* global React, ReactDOM, axios */
const { useState, useEffect } = React;
const { createRoot } = ReactDOM;

const App = () => {
    const [sunriseSunsets, setSunriseSunsets] = useState([]);
    const [formData, setFormData] = useState({ id: null, date: '', latitude: null, longitude: null, sunrise: '', sunset: '' });
    const [isEditing, setIsEditing] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchSunriseSunsets();
    }, []);

    const fetchSunriseSunsets = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/sunrise-sunset');
            setSunriseSunsets(response.data);
        } catch (err) {
            setError('Не удалось загрузить записи sunrise/sunset');
            console.error('Ошибка при загрузке sunrise/sunset:', err);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: name === 'latitude' || name === 'longitude' ? parseFloat(value) : value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (isEditing) {
                await axios.put(`http://localhost:8080/api/sunrise-sunset/${formData.id}`, formData);
            } else {
                await axios.post('http://localhost:8080/api/sunrise-sunset', formData);
            }
            fetchSunriseSunsets();
            resetForm();
        } catch (err) {
            setError('Не удалось сохранить запись');
            console.error('Ошибка при сохранении записи:', err.response?.data || err.message);
        }
    };

    const handleEdit = (sunriseSunset) => {
        setFormData({
            id: sunriseSunset.id,
            date: sunriseSunset.date,
            latitude: sunriseSunset.latitude,
            longitude: sunriseSunset.longitude,
            sunrise: sunriseSunset.sunrise,
            sunset: sunriseSunset.sunset
        });
        setIsEditing(true);
    };

    const handleDelete = async (id) => {
        try {
            await axios.delete(`http://localhost:8080/api/sunrise-sunset/${id}`);
            fetchSunriseSunsets();
        } catch (err) {
            setError('Не удалось удалить запись');
            console.error('Ошибка при удалении записи:', err);
        }
    };

    const resetForm = () => {
        setFormData({ id: null, date: '', latitude: null, longitude: null, sunrise: '', sunset: '' });
        setIsEditing(false);
    };

    return (
        <div className="container mx-auto p-4">
            <nav className="bg-blue-600 text-white p-4 rounded-md mb-6">
                <h1 className="text-2xl font-bold flex items-center">
                    <i className="fas fa-sun mr-2"></i> Менеджер Sunrise-Sunset
                </h1>
            </nav>

            {error && (
                <div className="bg-red-100 text-red-700 p-4 rounded-md mb-6 flex items-center">
                    <i className="fas fa-exclamation-circle mr-2"></i> {error}
                </div>
            )}

            <div className="bg-white p-6 rounded-md shadow-md mb-6">
                <h2 className="text-xl font-semibold mb-4 flex items-center">
                    <i className="fas fa-plus-circle mr-2 text-blue-600"></i> {isEditing ? 'Обновить запись' : 'Добавить новую запись'}
                </h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 flex items-center">
                            <i className="fas fa-calendar-alt mr-2 text-gray-500"></i> Дата
                        </label>
                        <input
                            type="text"
                            name="date"
                            value={formData.date}
                            onChange={handleInputChange}
                            className="mt-1 block w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Например, 2025-03-03"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 flex items-center">
                            <i className="fas fa-map-marker-alt mr-2 text-gray-500"></i> Широта (Latitude)
                        </label>
                        <input
                            type="number"
                            step="any"
                            name="latitude"
                            value={formData.latitude || ''}
                            onChange={handleInputChange}
                            className="mt-1 block w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Например, 53.9"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 flex items-center">
                            <i className="fas fa-map-marker-alt mr-2 text-gray-500"></i> Долгота (Longitude)
                        </label>
                        <input
                            type="number"
                            step="any"
                            name="longitude"
                            value={formData.longitude || ''}
                            onChange={handleInputChange}
                            className="mt-1 block w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Например, 27.6"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 flex items-center">
                            <i className="fas fa-sun mr-2 text-gray-500"></i> Восход (Sunrise)
                        </label>
                        <input
                            type="text"
                            name="sunrise"
                            value={formData.sunrise}
                            onChange={handleInputChange}
                            className="mt-1 block w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Например, 06:41"
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 flex items-center">
                            <i className="fas fa-moon mr-2 text-gray-500"></i> Закат (Sunset)
                        </label>
                        <input
                            type="text"
                            name="sunset"
                            value={formData.sunset}
                            onChange={handleInputChange}
                            className="mt-1 block w-full p-2 border rounded-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="Например, 18:04"
                            required
                        />
                    </div>
                    <div className="flex space-x-4">
                        <button
                            type="submit"
                            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 flex items-center"
                        >
                            <i className={isEditing ? "fas fa-save mr-2" : "fas fa-plus mr-2"}></i>
                            {isEditing ? 'Обновить' : 'Создать'}
                        </button>
                        {isEditing && (
                            <button
                                type="button"
                                onClick={resetForm}
                                className="bg-gray-600 text-white px-4 py-2 rounded-md hover:bg-gray-700 flex items-center"
                            >
                                <i className="fas fa-times mr-2"></i> Отмена
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {sunriseSunsets.map(sunriseSunset => (
                    <div key={sunriseSunset.id} className="bg-white p-6 rounded-md shadow-md">
                        <h3 className="text-lg font-semibold flex items-center">
                            <i className="fas fa-calendar-day mr-2 text-blue-600"></i> Дата: {sunriseSunset.date}
                        </h3>
                        <p className="text-gray-600">Широта: {sunriseSunset.latitude}</p>
                        <p className="text-gray-600">Долгота: {sunriseSunset.longitude}</p>
                        <p className="text-gray-600">Восход: {sunriseSunset.sunrise}</p>
                        <p className="text-gray-600">Закат: {sunriseSunset.sunset}</p>
                        <div className="mt-4 flex space-x-4">
                            <button
                                onClick={() => handleEdit(sunriseSunset)}
                                className="bg-yellow-500 text-white px-4 py-2 rounded-md hover:bg-yellow-600 flex items-center"
                            >
                                <i className="fas fa-edit mr-2"></i> Редактировать
                            </button>
                            <button
                                onClick={() => handleDelete(sunriseSunset.id)}
                                className="bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600 flex items-center"
                            >
                                <i className="fas fa-trash-alt mr-2"></i> Удалить
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

const root = createRoot(document.getElementById('root'));
root.render(<App />);